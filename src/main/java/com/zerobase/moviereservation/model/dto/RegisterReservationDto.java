package com.zerobase.moviereservation.model.dto;

import com.zerobase.moviereservation.model.type.CancelType;
import com.zerobase.moviereservation.model.type.ReservedType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RegisterReservationDto {

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Request {

    private Long userId;
    private Long scheduleId;
    private List<Long> seatIds;
    private CancelType cancel;
    private ReservedType reserved;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {

    private String username;
    private String movieTitle;
    private String theaterName;
    private List<String> seats;
    private CancelType cancel;
    private ReservedType reserved;

    public static Response from(ReservationDto reservationDto) {
      return Response.builder()
          .username(reservationDto.getUsername())
          .movieTitle(reservationDto.getMovieTitle())
          .theaterName(reservationDto.getTheaterName())
          .seats(reservationDto.getSeats())
          .cancel(reservationDto.getCancel())
          .reserved(reservationDto.getReserved())
          .build();
    }
  }
}
