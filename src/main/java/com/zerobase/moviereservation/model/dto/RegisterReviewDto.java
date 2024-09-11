package com.zerobase.moviereservation.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @Size(max = 80, message = "리뷰 내용은 최대 80자까지 입력 가능합니다.")
    private String content;

    @NotNull
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하이어야 합니다.")
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
