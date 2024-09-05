package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_TITLE;
import static com.zerobase.moviereservation.exception.type.ErrorCode.MOVIE_NOT_FOUND;

import com.zerobase.moviereservation.entity.Movie;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.MovieDto;
import com.zerobase.moviereservation.model.dto.RegisterMovieDto.Request;
import com.zerobase.moviereservation.model.dto.UpdateMovieDto;
import com.zerobase.moviereservation.repository.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {

  private final MovieRepository movieRepository;

  @Override
  @Transactional
  public MovieDto registerMovie(Request request) {
    if (this.movieRepository.existsByMovieTitle(request.getTitle())) {
      throw new CustomException(ALREADY_EXISTED_TITLE);
    }

    Movie movie = this.movieRepository.save(Movie.builder()
        .title(request.getTitle())
        .director(request.getDirector())
        .genre(request.getGenre())
        .runningTime(request.getRunningTime())
        .releaseDate(request.getReleaseDate())
        .rating(request.getRating())
        .build());

    return MovieDto.fromEntity(movie);
  }

  // 추후 평점 등록용 update 함수
  @Override
  @Transactional
  public MovieDto updateMovie(Long movieId, UpdateMovieDto.Request request) {
    Movie movie = this.movieRepository.findById(movieId)
        .orElseThrow(() -> new CustomException(MOVIE_NOT_FOUND));

    movie.setRating(request.getRating());

    return MovieDto.fromEntity(movie);
  }

  @Override
  @Transactional
  public void deleteMovie(Long movieId) {
    Movie movie = this.movieRepository.findById(movieId)
        .orElseThrow(() -> new CustomException(MOVIE_NOT_FOUND));

    this.movieRepository.delete(movie);
  }
}
