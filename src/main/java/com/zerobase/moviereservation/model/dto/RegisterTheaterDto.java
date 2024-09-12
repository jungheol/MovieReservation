package com.zerobase.moviereservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RegisterTheaterDto {

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Request {

    private String theaterName;
    private String address;
    @Builder.Default
    private int seatCount = 100;
  }


  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {

    private String theaterName;
    private String address;

    public static Response from(TheaterDto theaterDto) {
      return Response.builder()
          .theaterName(theaterDto.getTheaterName())
          .address(theaterDto.getAddress())
          .build();
    }
  }
}
