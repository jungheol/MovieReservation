package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.aop.RedisLock;
import com.zerobase.moviereservation.entity.Movie;
import com.zerobase.moviereservation.model.document.MovieDocument;
import com.zerobase.moviereservation.repository.MovieRepository;
import com.zerobase.moviereservation.repository.ReviewRepository;
import com.zerobase.moviereservation.repository.document.SearchMovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MovieRatingService {

  private final MovieRepository movieRepository;
  private final ReviewRepository reviewRepository;
  private final SearchMovieRepository searchMovieRepository;

  @RedisLock(keys = "'movie_lock:' + #movie.id")
  public void updateMovieRating(Movie movie) {
    Double avgRating = reviewRepository.findAverageRatingByMovieId(movie.getId());
    if (avgRating != null) {
      movie.setRating(avgRating);
    } else { // null 일 때 == 해당 영화 리뷰가 (삭제되어) 하나도 없을 때
      movie.setRating(0.0);
    }

    movieRepository.save(movie);

    // MovieDocument 에 평균 평점 업데이트
    updateMovieDocumentRating(movie);
  }

  private void updateMovieDocumentRating(Movie movie) {
    searchMovieRepository.findById(movie.getId())
        .ifPresentOrElse(
            movieDocument -> {
              movieDocument.setRating(movie.getRating());
              searchMovieRepository.save(movieDocument);
            },
            () -> {
              MovieDocument newMovieDocument = new MovieDocument(
                  movie.getId(),
                  movie.getTitle(),
                  movie.getDirector(),
                  movie.getGenre(),
                  movie.getRunningMinute(),
                  movie.getReleaseDate(),
                  movie.getRating()
              );
              searchMovieRepository.save(newMovieDocument);
            }
        );
  }
}
