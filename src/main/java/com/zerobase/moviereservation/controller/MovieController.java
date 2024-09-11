package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.model.document.MovieDocument;
import com.zerobase.moviereservation.model.dto.MovieDto;
import com.zerobase.moviereservation.model.dto.RegisterMovieDto;
import com.zerobase.moviereservation.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

  private final MovieService movieService;

  @PostMapping
  public RegisterMovieDto.Response register(
      @RequestBody RegisterMovieDto.Request request
  ) {
    return RegisterMovieDto.Response.from(
        this.movieService.registerMovie(request));
  }

  @GetMapping("/{movieId}")
  public MovieDto getMovie(
      @PathVariable("movieId") Long movieId
  ) {
    return movieService.getMovie(movieId);
  }

  @GetMapping("/search/title")
  public Page<MovieDocument> searchMoviesByTitle(
      @RequestParam("title") String title,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "5") int size
  ) {
    return movieService.searchMoviesByTitle(title, page, size);
  }

  @GetMapping("/search/genre")
  public Page<MovieDocument> searchMoviesByGenre(
      @RequestParam("genre") String genre,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "5") int size
  ) {
    return movieService.searchMoviesByGenre(genre, page, size);
  }

  @DeleteMapping("/{movieId}")
  public ResponseEntity<?> deleteMovie(
      @PathVariable("movieId") Long movieId
  ) {
    this.movieService.deleteMovie(movieId);
    return ResponseEntity.ok("영화 정보가 삭제되었습니다.");
  }
}
