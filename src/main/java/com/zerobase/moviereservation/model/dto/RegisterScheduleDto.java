package com.zerobase.moviereservation.model.dto;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RegisterScheduleDto {

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Request {

    private Long movieId;
    private Long theaterId;
    private LocalTime startTime;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {

    private String movieTitle;
    private String theaterName;
    private LocalTime startTime;
    private LocalTime endTime;

    public static Response from(ScheduleDto scheduleDto) {
      return Response.builder()
          .movieTitle(scheduleDto.getMovieTitle())
          .theaterName(scheduleDto.getTheaterName())
          .startTime(scheduleDto.getStartTime())
          .endTime(scheduleDto.getEndTime())
          .build();
    }
  }

}
