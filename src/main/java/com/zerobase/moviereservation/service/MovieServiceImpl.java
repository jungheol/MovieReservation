package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_TITLE;
import static com.zerobase.moviereservation.exception.type.ErrorCode.MOVIE_NOT_FOUND;

import com.zerobase.moviereservation.entity.Movie;
import com.zerobase.moviereservation.model.document.MovieDocument;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.MovieDto;
import com.zerobase.moviereservation.model.dto.RegisterMovieDto.Request;
import com.zerobase.moviereservation.repository.MovieRepository;
import com.zerobase.moviereservation.repository.document.SearchMovieRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {

  private final MovieRepository movieRepository;
  private final SearchMovieRepository searchMovieRepository;

  @Override
  @Transactional
  public MovieDto registerMovie(Request request) {
    if (this.movieRepository.existsByTitle(request.getTitle())) {
      throw new CustomException(ALREADY_EXISTED_TITLE);
    }

    Movie movie = this.movieRepository.save(Movie.builder()
        .title(request.getTitle())
        .director(request.getDirector())
        .genre(request.getGenre())
        .runningTime(request.getRunningTime())
        .releaseDate(request.getReleaseDate())
        .build());

    saveDocument(movie);
    return MovieDto.fromEntity(movie);
  }

  @Override
  public List<MovieDocument> searchMoviesByTitle(String title) {
    List<MovieDocument> movies = searchMovieRepository.findByTitleContaining(title);
    if (movies.isEmpty()) {
      throw new CustomException(MOVIE_NOT_FOUND);
    }
    return movies;
  }

  @Override
  public List<MovieDocument> searchMoviesByGenre(String genre) {
    List<MovieDocument> movies = searchMovieRepository.findByGenreContaining(genre);
    if (movies.isEmpty()) {
      throw new CustomException(MOVIE_NOT_FOUND);
    }
    return movies;
  }

  private MovieDocument saveDocument(Movie movie) {
    MovieDocument movieDocument = new MovieDocument();
    movieDocument.setId(movie.getId());
    movieDocument.setTitle(movie.getTitle());
    movieDocument.setDirector(movie.getDirector());
    movieDocument.setGenre(movie.getGenre());
    movieDocument.setRunningTime(movie.getRunningTime());
    movieDocument.setReleaseDate(movie.getReleaseDate());

    return searchMovieRepository.save(movieDocument);
  }

  @Override
  public MovieDto getMovie(Long movieId) {
    return MovieDto.fromEntity(movieRepository.findById(movieId)
        .orElseThrow(() -> new CustomException(MOVIE_NOT_FOUND)));
  }

  @Override
  @Transactional
  public void deleteMovie(Long movieId) {
    Movie movie = this.movieRepository.findById(movieId)
        .orElseThrow(() -> new CustomException(MOVIE_NOT_FOUND));

    this.movieRepository.delete(movie);
    this.searchMovieRepository.deleteById(movieId);
  }
}
