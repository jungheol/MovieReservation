package com.zerobase.moviereservation.model.dto;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RegisterMovieDto {

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Request {

    private String title;
    private String director;
    private String genre;
    private String runningTime;
    private LocalDate releaseDate;
    private Integer rating = 0;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {

    private String title;
    private String director;
    private String genre;
    private String runningTime;
    private LocalDate releaseDate;
    private Integer rating;

    public static Response from(MovieDto movieDto) {
      return Response.builder()
          .title(movieDto.getTitle())
          .director(movieDto.getDirector())
          .genre(movieDto.getGenre())
          .runningTime(movieDto.getRunningTime())
          .releaseDate(movieDto.getReleaseDate())
          .rating(movieDto.getRating())
          .build();
    }
  }
}
