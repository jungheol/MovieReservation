package com.zerobase.moviereservation.repository;

import com.zerobase.moviereservation.entity.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  List<Reservation> findByScheduleIdAndSeatIdIn(Long scheduleId, List<Long> seatIds);

  List<Reservation> findByUserIdAndScheduleId(Long userId, Long scheduleId);
}
