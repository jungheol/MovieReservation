package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_SCHEDULE;
import static com.zerobase.moviereservation.exception.type.ErrorCode.MOVIE_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.SCHEDULE_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.THEATER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.moviereservation.entity.Movie;
import com.zerobase.moviereservation.entity.Schedule;
import com.zerobase.moviereservation.entity.Theater;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterScheduleDto;
import com.zerobase.moviereservation.model.dto.ScheduleDto;
import com.zerobase.moviereservation.model.dto.UpdateScheduleDto;
import com.zerobase.moviereservation.repository.MovieRepository;
import com.zerobase.moviereservation.repository.ScheduleRepository;
import com.zerobase.moviereservation.repository.TheaterRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

  @Mock
  private ScheduleRepository scheduleRepository;

  @Mock
  private MovieRepository movieRepository;

  @Mock
  private TheaterRepository theaterRepository;

  @InjectMocks
  private ScheduleServiceImpl scheduleServiceImpl;

  private RegisterScheduleDto.Request registerScheduleDto;

  private UpdateScheduleDto.Request updateScheduleDto;

  private Schedule schedule;

  private Movie movie;

  private Theater theater;

  @BeforeEach
  void setUp() {
    movie = new Movie();
    movie.setId(1L);
    movie.setTitle("Sample Movie");
    movie.setDirector("Sample Director");
    movie.setGenre("Action");
    movie.setRunningTime("120");
    movie.setReleaseDate(LocalDate.parse("2024-09-01"));

    theater = new Theater();
    theater.setId(1L);
    theater.setTheaterName("Sample Theater");
    theater.setAddress("123 Main Street");

    registerScheduleDto = new RegisterScheduleDto.Request();
    registerScheduleDto.setMovieId(1L);
    registerScheduleDto.setTheaterId(1L);
    registerScheduleDto.setStartTime(LocalTime.parse("13:00:00"));

    updateScheduleDto = new UpdateScheduleDto.Request();
    updateScheduleDto.setMovieId(2L);
    updateScheduleDto.setTheaterId(2L);
    updateScheduleDto.setStartTime(LocalTime.parse("14:00:00"));

    schedule = Schedule.builder()
        .movie(movie)
        .theater(theater)
        .startTime(registerScheduleDto.getStartTime())
        .build();
  }

  @Test
  @DisplayName("스케쥴 등록 성공")
  void testRegister_Success() {
    // given
    when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
    when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));
    when(scheduleRepository.existsByTheaterIdAndStartTime(theater.getId(),
        registerScheduleDto.getStartTime())).thenReturn(false);
    when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

    // when
    ScheduleDto result = scheduleServiceImpl.registerSchedule(registerScheduleDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getMovieTitle()).isEqualTo(movie.getTitle());
    assertThat(result.getTheaterName()).isEqualTo(theater.getTheaterName());
    assertThat(result.getStartTime()).isEqualTo(registerScheduleDto.getStartTime());

    // verify
    verify(movieRepository).findById(registerScheduleDto.getMovieId());
    verify(theaterRepository).findById(registerScheduleDto.getTheaterId());
    verify(scheduleRepository).existsByTheaterIdAndStartTime(theater.getId(),
        registerScheduleDto.getStartTime());
    verify(scheduleRepository).save(any(Schedule.class));
  }

  @Test
  @DisplayName("스케쥴 등록 실패 - 해당 영화 없음")
  void testRegister_Fail_MovieNotFound() {
    // given
    when(movieRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> scheduleServiceImpl.registerSchedule(registerScheduleDto));
    assertEquals(MOVIE_NOT_FOUND, exception.getErrorCode());

    // verify
    verify(movieRepository).findById(registerScheduleDto.getMovieId());
  }

  @Test
  @DisplayName("스케쥴 등록 실패 - 해당 극장 없음")
  void testRegister_Fail_TheaterNotFound() {
    // given
    when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
    when(theaterRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> scheduleServiceImpl.registerSchedule(registerScheduleDto));
    assertEquals(THEATER_NOT_FOUND, exception.getErrorCode());

    // verify
    verify(movieRepository).findById(registerScheduleDto.getMovieId());
    verify(theaterRepository).findById(registerScheduleDto.getTheaterId());
  }

  @Test
  @DisplayName("스케쥴 등록 실패 - 해당 일정 존재")
  void testRegister_Fail_AlreadyExistedSchedule() {
    // given
    when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
    when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));
    when(scheduleRepository.existsByTheaterIdAndStartTime(theater.getId(),
        registerScheduleDto.getStartTime()))
        .thenReturn(true);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> scheduleServiceImpl.registerSchedule(registerScheduleDto));
    assertEquals(ALREADY_EXISTED_SCHEDULE, exception.getErrorCode());

    // verify
    verify(movieRepository).findById(registerScheduleDto.getMovieId());
    verify(theaterRepository).findById(registerScheduleDto.getTheaterId());
    verify(scheduleRepository).existsByTheaterIdAndStartTime(theater.getId(),
        registerScheduleDto.getStartTime());
  }

  @Test
  @DisplayName("스케쥴 수정 성공")
  void testUpdate_Success() {
    // given
    when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
    when(movieRepository.findById(2L)).thenReturn(Optional.of(movie));
    when(theaterRepository.findById(2L)).thenReturn(Optional.of(theater));
    when(scheduleRepository.existsByTheaterAndStartTimeAndIdNot(theater,
        updateScheduleDto.getStartTime(), 1L))
        .thenReturn(false);
    when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

    // when
    ScheduleDto result = scheduleServiceImpl.updateSchedule(1L, updateScheduleDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getMovieTitle()).isEqualTo(movie.getTitle());
    assertThat(result.getTheaterName()).isEqualTo(theater.getTheaterName());
    assertThat(result.getStartTime()).isEqualTo(updateScheduleDto.getStartTime());

    // verify
    verify(scheduleRepository).findById(1L);
    verify(movieRepository).findById(updateScheduleDto.getMovieId());
    verify(theaterRepository).findById(updateScheduleDto.getTheaterId());
    verify(scheduleRepository).existsByTheaterAndStartTimeAndIdNot(theater,
        updateScheduleDto.getStartTime(), 1L);
    verify(scheduleRepository).save(any(Schedule.class));
  }

  @Test
  @DisplayName("스케쥴 수정 실패 - 해당 스케쥴 없음")
  void testUpdate_Fail_ScheduleNotFound() {
    // given
    when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> scheduleServiceImpl.updateSchedule(1L, updateScheduleDto));
    assertEquals(SCHEDULE_NOT_FOUND, exception.getErrorCode());

    // verify
    verify(scheduleRepository).findById(1L);
  }

  @Test
  @DisplayName("스케쥴 수정 실패 - 해당 영화 없음")
  void testUpdate_Fail_MovieNotFound() {
    // given
    when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
    when(movieRepository.findById(2L)).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> scheduleServiceImpl.updateSchedule(1L, updateScheduleDto));
    assertEquals(MOVIE_NOT_FOUND, exception.getErrorCode());

    // verify
    verify(scheduleRepository).findById(1L);
    verify(movieRepository).findById(updateScheduleDto.getMovieId());
  }

  @Test
  @DisplayName("스케쥴 수정 실패 - 해당 극장 없음")
  void testUpdate_Fail_TheaterNotFound() {
    // given
    when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
    when(movieRepository.findById(2L)).thenReturn(Optional.of(movie));
    when(theaterRepository.findById(2L)).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> scheduleServiceImpl.updateSchedule(1L, updateScheduleDto));
    assertEquals(THEATER_NOT_FOUND, exception.getErrorCode());

    // verify
    verify(scheduleRepository).findById(1L);
    verify(movieRepository).findById(updateScheduleDto.getMovieId());
    verify(theaterRepository).findById(updateScheduleDto.getTheaterId());
  }

  @Test
  @DisplayName("스케쥴 수정 실패 - 해당 일정 존재")
  void testUpdate_Fail_AlreadyExistedSchedule() {
    // given
    when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
    when(movieRepository.findById(2L)).thenReturn(Optional.of(movie));
    when(theaterRepository.findById(2L)).thenReturn(Optional.of(theater));
    when(scheduleRepository.existsByTheaterAndStartTimeAndIdNot(theater,
        updateScheduleDto.getStartTime(), 1L))
        .thenReturn(true);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> scheduleServiceImpl.updateSchedule(1L, updateScheduleDto));
    assertEquals(ALREADY_EXISTED_SCHEDULE, exception.getErrorCode());

    // verify
    verify(scheduleRepository).findById(1L);
    verify(movieRepository).findById(updateScheduleDto.getMovieId());
    verify(theaterRepository).findById(updateScheduleDto.getTheaterId());
    verify(scheduleRepository).existsByTheaterAndStartTimeAndIdNot(theater,
        updateScheduleDto.getStartTime(), 1L);
  }

  @Test
  @DisplayName("스케쥴 삭제 성공")
  void testDelete_Success() {
    // given
    Long scheduleId = 1L;
    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

    // when & then
    scheduleServiceImpl.deleteSchedule(scheduleId);

    // verify
    verify(scheduleRepository).findById(scheduleId);
    verify(scheduleRepository).delete(schedule);
  }

  @Test
  @DisplayName("스케쥴 삭제 실패")
  void testDelete_Fail() {
    // given
    Long scheduleId = 1L;
    when(scheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> scheduleServiceImpl.deleteSchedule(scheduleId));
    assertEquals(SCHEDULE_NOT_FOUND, exception.getErrorCode());

    // verify
    verify(scheduleRepository).findById(scheduleId);
  }
}