package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.model.dto.RegisterMovieDto;
import com.zerobase.moviereservation.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

  private final MovieService movieService;

  @PostMapping
  public RegisterMovieDto.Response register(@RequestBody RegisterMovieDto.Request request) {
    return RegisterMovieDto.Response.from(this.movieService.registerMovie(request));
  }

  @DeleteMapping("/{movieId}")
  public ResponseEntity<?> deleteMovie(
      @PathVariable("movieId") Long movieId
  ) {
    this.movieService.deleteMovie(movieId);
    return ResponseEntity.ok("영화 정보가 삭제되었습니다.");
  }
}
