package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_CANCELED_RESERVATION;
import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_RESERVATION;
import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_RESERVED_SEAT;
import static com.zerobase.moviereservation.exception.type.ErrorCode.AUTHORIZATION_ERROR;
import static com.zerobase.moviereservation.exception.type.ErrorCode.RESERVATION_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.SCHEDULE_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.SEAT_NOT_VALID;
import static com.zerobase.moviereservation.exception.type.ErrorCode.USER_NOT_FOUND;

import com.zerobase.moviereservation.entity.Reservation;
import com.zerobase.moviereservation.entity.Schedule;
import com.zerobase.moviereservation.entity.Seat;
import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterReservationDto.Request;
import com.zerobase.moviereservation.model.dto.ReservationDto;
import com.zerobase.moviereservation.repository.ReservationRepository;
import com.zerobase.moviereservation.repository.ScheduleRepository;
import com.zerobase.moviereservation.repository.SeatRepository;
import com.zerobase.moviereservation.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
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

  @Override
  @Transactional
  public List<ReservationDto> registerReservation(Request request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Schedule schedule = scheduleRepository.findById(request.getScheduleId())
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

    String lockKey = "reservation_lock:" + request.getScheduleId() + ":" + request.getSeatIds();
    String lockValue = UUID.randomUUID().toString();

    boolean isLocked = redisLockService.lock(lockKey, lockValue, 5);

    if (!isLocked) {
      throw new CustomException(ALREADY_RESERVED_SEAT);
    }

    try {
      // scheduleId & seatId 가 있고, cancel == "N" 일 때 예외 발생
      if (this.reservationRepository.findByScheduleIdAndSeatsIdIn(schedule.getId(),
          request.getSeatIds()).stream().anyMatch(r -> "N".equals(r.getCancel()))) {
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

      return List.of(ReservationDto.fromEntity(reservation));
    } finally {
      redisLockService.unlock(lockKey, lockValue);
    }
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
    if ("Y".equals(reservation.getCancel())) {
      throw new CustomException(ALREADY_CANCELED_RESERVATION);
    }

    // 예약의 취소 상태를 업데이트합니다.
    reservation.setCancel("Y");
    reservation.setReserved("N");
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
