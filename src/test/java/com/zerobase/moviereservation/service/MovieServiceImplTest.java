package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_TITLE;
import static com.zerobase.moviereservation.exception.type.ErrorCode.MOVIE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.moviereservation.entity.Movie;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.MovieDto;
import com.zerobase.moviereservation.model.dto.RegisterMovieDto;
import com.zerobase.moviereservation.repository.MovieRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

  @Mock
  private MovieRepository movieRepository;

  @InjectMocks
  private MovieServiceImpl movieServiceImpl;

  private RegisterMovieDto.Request registerMovieDto;

  private Movie movie;

  @BeforeEach
  void setUp() {
    registerMovieDto = new RegisterMovieDto.Request();
    registerMovieDto.setTitle("title1");
    registerMovieDto.setDirector("director1");
    registerMovieDto.setGenre("genre1");
    registerMovieDto.setRunningTime("runningTime1");
    registerMovieDto.setReleaseDate(LocalDate.parse("2024-09-01"));
    registerMovieDto.setRating(0);

    movie = Movie.builder()
        .title(registerMovieDto.getTitle())
        .director(registerMovieDto.getDirector())
        .genre(registerMovieDto.getGenre())
        .runningTime(registerMovieDto.getRunningTime())
        .releaseDate(registerMovieDto.getReleaseDate())
        .rating(registerMovieDto.getRating())
        .build();
  }

  @Test
  @DisplayName("영화 등록 성공")
  void testRegister_Success() {
    // given
    when(movieRepository.existsByMovieTitle(registerMovieDto.getTitle())).thenReturn(false);
    when(movieRepository.save(any(Movie.class))).thenReturn(movie);

    // when
    MovieDto result = movieServiceImpl.registerMovie(registerMovieDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo(registerMovieDto.getTitle());
    assertThat(result.getDirector()).isEqualTo(registerMovieDto.getDirector());
    assertThat(result.getGenre()).isEqualTo(registerMovieDto.getGenre());
    assertThat(result.getRunningTime()).isEqualTo(registerMovieDto.getRunningTime());
    assertThat(result.getReleaseDate()).isEqualTo(registerMovieDto.getReleaseDate());
    assertThat(result.getRating()).isEqualTo(registerMovieDto.getRating());

    // verify
    verify(movieRepository).existsByMovieTitle(registerMovieDto.getTitle());
    verify(movieRepository).save(any(Movie.class));
  }

  @Test
  @DisplayName("영화 등록 실패")
  void testRegister_Fail() {
    // given
    when(movieRepository.existsByMovieTitle(registerMovieDto.getTitle())).thenReturn(true);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> movieServiceImpl.registerMovie(registerMovieDto));
    assertEquals(ALREADY_EXISTED_TITLE, exception.getErrorCode());

    // verify
    verify(movieRepository).existsByMovieTitle(registerMovieDto.getTitle());
  }

  @Test
  @DisplayName("영화 정보 삭제 성공")
  void testDelete_Success() {
    // given
    Long movieId = 1L;
    when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

    // when & then
    movieServiceImpl.deleteMovie(movieId);

    // verify
    verify(movieRepository).findById(movieId);
    verify(movieRepository).delete(movie);
  }

  @Test
  @DisplayName("영화 정보 삭제 실패")
  void testDelete_Fail() {
    // given
    Long movieId = 1L;
    when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> movieServiceImpl.deleteMovie(movieId));
    assertEquals(MOVIE_NOT_FOUND, exception.getErrorCode());

    // verify
    verify(movieRepository).findById(movieId);
  }
}