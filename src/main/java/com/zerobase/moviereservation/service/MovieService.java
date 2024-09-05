package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.MovieDto;
import com.zerobase.moviereservation.model.dto.RegisterMovieDto;
import com.zerobase.moviereservation.model.dto.UpdateMovieDto;

public interface MovieService {

  MovieDto registerMovie(RegisterMovieDto.Request request);

  MovieDto updateMovie(Long movieId, UpdateMovieDto.Request request);

  void deleteMovie(Long movieId);
}
