package com.zerobase.moviereservation.repository;

import com.zerobase.moviereservation.entity.Reservation;
import com.zerobase.moviereservation.entity.Seat;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  List<Reservation> findByScheduleIdAndSeatsIdIn(Long scheduleId, List<Long> seatsIds);

  @Query("SELECT r FROM Reservation r JOIN r.seats s WHERE r.schedule.id = :scheduleId AND s.id IN :seatIds AND r.cancel = 'N'")
  List<Reservation> findActiveReservationsByScheduleIdAndSeatsIdIn(
      @Param("scheduleId") Long scheduleId,
      @Param("seatIds") List<Long> seatIds
  );

  Page<Reservation> findByUserId(Long userId, Pageable pageable);

  @Query("SELECT s FROM Reservation r JOIN r.seats s WHERE r.schedule.id = :scheduleId AND r.cancel = 'N'")
  List<Seat> findReservedSeatsByScheduleId(@Param("scheduleId") Long scheduleId);
}
