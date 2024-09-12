package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_CANCELED_RESERVATION;
import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_RESERVATION;
import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_SCHEDULE;
import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_RESERVED_SEAT;
import static com.zerobase.moviereservation.exception.type.ErrorCode.PAYMENT_FAILED;
import static com.zerobase.moviereservation.exception.type.ErrorCode.RESERVATION_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.SCHEDULE_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.SEAT_NOT_VALID;

import com.zerobase.moviereservation.entity.Payment;
import com.zerobase.moviereservation.entity.Reservation;
import com.zerobase.moviereservation.entity.Schedule;
import com.zerobase.moviereservation.entity.Seat;
import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterReservationDto.Request;
import com.zerobase.moviereservation.model.dto.ReservationDto;
import com.zerobase.moviereservation.model.type.CancelType;
import com.zerobase.moviereservation.model.type.PaymentType;
import com.zerobase.moviereservation.model.type.ReservedType;
import com.zerobase.moviereservation.repository.ReservationRepository;
import com.zerobase.moviereservation.repository.ScheduleRepository;
import com.zerobase.moviereservation.repository.SeatRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;
  private final ScheduleRepository scheduleRepository;
  private final SeatRepository seatRepository;
  private final RedisLockService redisLockService;
  private final PaymentService paymentService;
  private final AuthenticationService authenticationService;

  @Override
  @Transactional
  public ReservationDto registerReservation(Request request) {
    User authuser = authenticationService.getAuthenticatedUser(request.getUserId());

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
          .user(authuser)
          .schedule(schedule)
          .seats(seats)
          .cancel(CancelType.N)
          .reserved(ReservedType.Y)
          .build());

      Integer amount = calculateAmount(schedule, seats);
      Payment payment = paymentService.processPayment(reservation, amount);
      if (!PaymentType.S.equals(payment.getStatus())) {
        throw new CustomException(PAYMENT_FAILED);
      }

      return ReservationDto.fromEntity(reservation);

    } finally {
      for (Long seatId : request.getSeatIds()) {
        redisLockService.unlock("seat_lock:" + seatId);
      }
      redisLockService.unlock("schedule_lock:" + schedule.getId());
    }
  }

  @Override
  @Transactional
  public ReservationDto canceledReservation(Long userId, Long reservationId) {
    authenticationService.getAuthenticatedUser(userId);

    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

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
  public Page<ReservationDto> getAllReservation(Long userId, int page, int size) {
    authenticationService.getAuthenticatedUser(userId);

    Pageable pageable = PageRequest.of(page, size);

    Page<Reservation> reservations = reservationRepository.findByUserId(userId, pageable);

    if (reservations.isEmpty()) {
      throw new CustomException(RESERVATION_NOT_FOUND);
    }

    return reservations.map(ReservationDto::fromEntity);
  }

  private Integer calculateAmount(Schedule schedule, List<Seat> seats) {
    // 좌석당 금액 10000원 책정
    return seats.size() * schedule.getPricePerSeat();
  }
}
