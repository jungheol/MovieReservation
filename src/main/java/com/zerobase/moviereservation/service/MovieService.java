package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.MovieDto;
import com.zerobase.moviereservation.model.dto.RegisterMovieDto;

public interface MovieService {

  MovieDto registerMovie(RegisterMovieDto.Request request);

  void deleteMovie(Long movieId);
}
