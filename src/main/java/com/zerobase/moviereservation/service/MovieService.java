package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.document.MovieDocument;
import com.zerobase.moviereservation.model.dto.MovieDto;
import com.zerobase.moviereservation.model.dto.RegisterMovieDto;
import org.springframework.data.domain.Page;

public interface MovieService {

  MovieDto registerMovie(RegisterMovieDto.Request request);

  MovieDto getMovie(Long movieId);

  Page<MovieDocument> searchMoviesByTitle(String title, int page, int size);

  Page<MovieDocument> searchMoviesByGenre(String genre, int page, int size);

  void deleteMovie(Long movieId);
}
