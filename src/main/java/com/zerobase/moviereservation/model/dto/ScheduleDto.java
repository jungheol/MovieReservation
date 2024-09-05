package com.zerobase.moviereservation.model.dto;

import com.zerobase.moviereservation.entity.Schedule;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {

  private Long id;
  private String movieTitle;
  private String theaterName;
  private LocalTime startTime;

  public static ScheduleDto fromEntity(Schedule schedule) {
    return ScheduleDto.builder()
        .id(schedule.getId())
        .movieTitle(schedule.getMovie().getTitle())
        .theaterName(schedule.getTheater().getTheaterName())
        .startTime(schedule.getStartTime())
        .build();
  }
}
