package com.zerobase.moviereservation.repository;

import com.zerobase.moviereservation.entity.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  List<Reservation> findByScheduleIdAndSeatsIdIn(Long scheduleId, List<Long> seatsIds);

  @Query("SELECT r FROM Reservation r JOIN r.seats s WHERE r.schedule.id = :scheduleId AND s.id IN :seatIds AND r.cancel = 'N'")
  List<Reservation> findActiveReservationsByScheduleIdAndSeatsIdIn(
      @Param("scheduleId") Long scheduleId,
      @Param("seatIds") List<Long> seatIds
  );

  List<Reservation> findByUserIdAndScheduleId(Long userId, Long scheduleId);

  @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.schedule.id = :scheduleId AND r.cancel = 'N'")
  List<Reservation> findActiveReservationsByUserIdAndScheduleId(
      @Param("userId") Long userId,
      @Param("scheduleId") Long scheduleId
  );

  List<Reservation> findByUserId(Long userId);
}
