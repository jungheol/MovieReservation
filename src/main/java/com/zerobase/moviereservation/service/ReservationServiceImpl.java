package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_RESERVATION;
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
import com.zerobase.moviereservation.model.dto.RegisterReservationDto.Response;
import com.zerobase.moviereservation.model.dto.ReservationDto;
import com.zerobase.moviereservation.repository.ReservationRepository;
import com.zerobase.moviereservation.repository.ScheduleRepository;
import com.zerobase.moviereservation.repository.SeatRepository;
import com.zerobase.moviereservation.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
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

  @Override
  @Transactional
  public List<ReservationDto> registerReservation(Request request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Schedule schedule = scheduleRepository.findById(request.getScheduleId())
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

    if (!this.reservationRepository.findByScheduleIdAndSeatIdIn(
        schedule.getId(), request.getSeatIds()).isEmpty()) {
      throw new CustomException(ALREADY_EXISTED_RESERVATION);
    }

    List<Seat> seats = seatRepository.findAllById(request.getSeatIds());
    if (seats.size() != request.getSeatIds().size()) {
      throw new CustomException(SEAT_NOT_VALID);
    }

    List<Reservation> reservations = seats.stream()
        .map(seat -> Reservation.builder()
            .user(user)
            .schedule(schedule)
            .seat(seat)
            .cancel(request.getCancel())
            .reserved(request.getReserved())
            .build())
        .collect(Collectors.toList());

    List<Reservation> savedReservations = reservationRepository.saveAll(reservations);

    return savedReservations.stream()
        .map(ReservationDto::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public List<ReservationDto> canceledReservation(Long userId, Long scheduleId) {
    List<Reservation> reservations = reservationRepository.findByUserIdAndScheduleId(userId, scheduleId);

    if (reservations.isEmpty()) {
      throw new CustomException(RESERVATION_NOT_FOUND);
    }
    reservations.forEach(reservation -> {
      reservation.setCancel("Y");
      reservation.setReserved("N");
      reservation.setCanceledAt(LocalDateTime.now());
    });

    List<Reservation> canceledReservations = reservationRepository.saveAll(reservations);

    return canceledReservations.stream()
        .map(ReservationDto::fromEntity)
        .collect(Collectors.toList());
  }
}
