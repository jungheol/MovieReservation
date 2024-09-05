package com.zerobase.moviereservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UpdateMovieDto {

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Request {
    private Integer rating;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {
    private String title;
    private Integer rating;

    public static Response from(MovieDto movieDto) {
     return Response.builder()
         .title(movieDto.getTitle())
         .rating(movieDto.getRating())
         .build();
    }
  }
}
