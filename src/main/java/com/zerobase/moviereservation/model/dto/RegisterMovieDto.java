package com.zerobase.moviereservation.model.dto;

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
    private Integer runningMinute;
    private LocalDate releaseDate;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {

    private String title;
    private String director;
    private String genre;
    private Integer runningMinute;
    private LocalDate releaseDate;
    private Double rating;

    public static Response from(MovieDto movieDto) {
      return Response.builder()
          .title(movieDto.getTitle())
          .director(movieDto.getDirector())
          .genre(movieDto.getGenre())
          .runningMinute(movieDto.getRunningMinute())
          .releaseDate(movieDto.getReleaseDate())
          .rating(movieDto.getRating())
          .build();
    }
  }
}
