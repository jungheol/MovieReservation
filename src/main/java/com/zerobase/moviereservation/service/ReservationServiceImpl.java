package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_CANCELED_RESERVATION;
import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_RESERVATION;
import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_SCHEDULE;
import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_RESERVED_SEAT;
import static com.zerobase.moviereservation.exception.type.ErrorCode.AUTHORIZATION_ERROR;
import static com.zerobase.moviereservation.exception.type.ErrorCode.PAYMENT_FAILED;
import static com.zerobase.moviereservation.exception.type.ErrorCode.RESERVATION_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.SCHEDULE_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.SEAT_NOT_VALID;
import static com.zerobase.moviereservation.exception.type.ErrorCode.USER_NOT_FOUND;

import com.zerobase.moviereservation.entity.Payment;
import com.zerobase.moviereservation.entity.Reservation;
import com.zerobase.moviereservation.entity.Schedule;
import com.zerobase.moviereservation.entity.Seat;
import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterReservationDto.Request;
import com.zerobase.moviereservation.model.dto.ReservationDto;
import com.zerobase.moviereservation.model.type.CancelType;
import com.zerobase.moviereservation.model.type.ReservedType;
import com.zerobase.moviereservation.repository.ReservationRepository;
import com.zerobase.moviereservation.repository.ScheduleRepository;
import com.zerobase.moviereservation.repository.SeatRepository;
import com.zerobase.moviereservation.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final UserRepository userRepository;
  private final ReservationRepository reservationRepository;
  private final ScheduleRepository scheduleRepository;
  private final SeatRepository seatRepository;
  private final RedisLockService redisLockService;
  private final PaymentService paymentService;

  @Override
  @Transactional
  public List<ReservationDto> registerReservation(Request request) {
    // 인증된 사용자의 이메일 가져오기
    UserDetails userDetails = (UserDetails) SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getPrincipal();

    User user = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Long authenticatedUserId = user.getId();

    // 예약하려는 userId와 인증된 사용자의 userId가 다를 경우 예외 발생
    if (!authenticatedUserId.equals(request.getUserId())) {
      throw new CustomException(AUTHORIZATION_ERROR);
    }

    user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Schedule schedule = scheduleRepository.findById(request.getScheduleId())
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

    for (Long seatId : request.getSeatIds()) {
      String seatLockKey = "seat_lock:" + seatId;
      if (!redisLockService.lock(seatLockKey, 10)) {
        throw new CustomException(ALREADY_RESERVED_SEAT);
      }
    }

    String scheduleLockKey = "schedule_lock:" + schedule.getId();
    if (!redisLockService.lock(scheduleLockKey, 10)) {
      throw new CustomException(ALREADY_EXISTED_SCHEDULE);
    }

    try {
      // scheduleId & seatId 가 있고, cancel == "N" 일 때 예외 발생
      if (this.reservationRepository.findByScheduleIdAndSeatsIdIn(schedule.getId(),
          request.getSeatIds()).stream().anyMatch(r -> CancelType.N.equals(r.getCancel()))) {
        throw new CustomException(ALREADY_EXISTED_RESERVATION);
      }

      List<Seat> seats = seatRepository.findAllById(request.getSeatIds());
      if (seats.size() != request.getSeatIds().size()) {
        throw new CustomException(SEAT_NOT_VALID);
      }

      Reservation reservation = this.reservationRepository.save(Reservation.builder()
          .user(user)
          .schedule(schedule)
          .seats(seats)
          .cancel(request.getCancel())
          .reserved(request.getReserved())
          .build());

      Integer amount = calculateAmount(schedule, seats);
      Payment payment = paymentService.processPayment(reservation, amount);
      if (!"success".equals(payment.getStatus())) {
        throw new CustomException(PAYMENT_FAILED);
      }

      return List.of(ReservationDto.fromEntity(reservation));

    } finally {
      for (Long seatId : request.getSeatIds()) {
        redisLockService.unlock("seat_lock:" + seatId);
      }
      redisLockService.unlock("schedule_lock:" + schedule.getId());
    }
  }

  private Integer calculateAmount(Schedule schedule, List<Seat> seats) {
    // 예: 각 좌석의 기본 금액에 추가 요금을 더하는 방식
    return seats.size() * schedule.getPricePerSeat();
  }

  @Override
  @Transactional
  public ReservationDto canceledReservation(Long userId, Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

    // 예약자와 예약내용이 일치하지 않을 때
    if (!reservation.getUser().getId().equals(userId)) {
      throw new CustomException(AUTHORIZATION_ERROR);
    }

    // 이미 취소된 예약인 경우
    if (CancelType.Y.equals(reservation.getCancel())) {
      throw new CustomException(ALREADY_CANCELED_RESERVATION);
    }

    // 예약의 취소 상태를 업데이트합니다.
    reservation.setCancel(CancelType.Y);
    reservation.setReserved(ReservedType.N);
    reservation.setCanceledAt(LocalDateTime.now());

    return ReservationDto.fromEntity(reservationRepository.save(reservation));
  }

  @Override
  public List<ReservationDto> getAllReservation(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    List<Reservation> reservations = reservationRepository.findByUserId(userId);

    if (reservations.isEmpty()) {
      throw new CustomException(RESERVATION_NOT_FOUND);
    }

    return reservations.stream()
        .map(ReservationDto::fromEntity)
        .collect(Collectors.toList());
  }
}
