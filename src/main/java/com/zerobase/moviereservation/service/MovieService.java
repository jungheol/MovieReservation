package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.document.MovieDocument;
import com.zerobase.moviereservation.model.dto.MovieDto;
import com.zerobase.moviereservation.model.dto.RegisterMovieDto;
import java.util.List;

public interface MovieService {

  MovieDto registerMovie(RegisterMovieDto.Request request);

  MovieDto getMovie(Long movieId);

  List<MovieDocument> searchMoviesByTitle(String title);

  List<MovieDocument> searchMoviesByGenre(String genre);

  void deleteMovie(Long movieId);
}
