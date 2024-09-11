package com.zerobase.moviereservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RegisterReviewDto {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

    private String content;
    private Integer rating;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Response {

    private Long reviewId;
    private String username;
    private String movieTitle;
    private String content;
    private Integer rating;

    public static Response from(ReviewDto reviewDto) {
      return Response.builder()
          .reviewId(reviewDto.getId())
          .username(reviewDto.getUsername())
          .movieTitle(reviewDto.getMovieTitle())
          .content(reviewDto.getContent())
          .rating(reviewDto.getRating())
          .build();
    }
  }
}
