package com.zerobase.moviereservation.repository;

import com.zerobase.moviereservation.entity.Schedule;
import com.zerobase.moviereservation.entity.Theater;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  boolean existsByTheaterIdAndEndTimeGreaterThanEqual(Long theaterId, LocalTime newStartTime);


  boolean existsByTheaterAndStartTimeAndIdNot(Theater theater, LocalTime startTime, Long scheduleId);

  @Query("SELECT s FROM Schedule s WHERE s.movie.id = :movieId")
  List<Schedule> findByMovieId(@Param("movieId") Long movieId);
}
