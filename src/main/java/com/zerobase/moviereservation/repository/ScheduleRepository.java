package com.zerobase.moviereservation.repository;

import com.zerobase.moviereservation.entity.Schedule;
import com.zerobase.moviereservation.entity.Theater;
import java.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  boolean existsByTheaterIdAndStartTime(Long theaterId, LocalTime startTime);

  boolean existsByTheaterAndStartTimeAndIdNot(Theater theater, LocalTime startTime, Long scheduleId);
}
