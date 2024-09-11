package com.zerobase.moviereservation.model.dto;

import com.zerobase.moviereservation.entity.Movie;
import java.time.LocalDate;
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
public class MovieDto {

  private Long id;
  private String title;
  private String director;
  private String genre;
  private Integer runningMinute;
  private LocalDate releaseDate;
  private Double rating;

  public static MovieDto fromEntity(Movie movie) {
    return MovieDto.builder()
        .id(movie.getId())
        .title(movie.getTitle())
        .director(movie.getDirector())
        .genre(movie.getGenre())
        .runningMinute(movie.getRunningMinute())
        .releaseDate(movie.getReleaseDate())
        .rating(movie.getRating())
        .build();
  }
}
