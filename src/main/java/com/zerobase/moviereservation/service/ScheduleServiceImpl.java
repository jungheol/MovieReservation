package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_SCHEDULE;
import static com.zerobase.moviereservation.exception.type.ErrorCode.MOVIE_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.SCHEDULE_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.THEATER_NOT_FOUND;

import com.zerobase.moviereservation.aop.RedisLock;
import com.zerobase.moviereservation.entity.Movie;
import com.zerobase.moviereservation.entity.Schedule;
import com.zerobase.moviereservation.entity.Theater;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.exception.type.ErrorCode;
import com.zerobase.moviereservation.model.dto.RegisterScheduleDto.Request;
import com.zerobase.moviereservation.model.dto.ScheduleDto;
import com.zerobase.moviereservation.model.dto.UpdateScheduleDto;
import com.zerobase.moviereservation.repository.MovieRepository;
import com.zerobase.moviereservation.repository.ScheduleRepository;
import com.zerobase.moviereservation.repository.TheaterRepository;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

  private final ScheduleRepository scheduleRepository;
  private final MovieRepository movieRepository;
  private final TheaterRepository theaterRepository;

  @Override
  @Transactional
  @RedisLock(keys = {"'schedule_lock:' + #request.theaterId + ':' + #request.startTime"})
  public ScheduleDto registerSchedule(Request request) {
    Movie movie = this.movieRepository.findById(request.getMovieId())
        .orElseThrow(() -> new CustomException(MOVIE_NOT_FOUND));

    Theater theater = this.theaterRepository.findById(request.getTheaterId())
        .orElseThrow(() -> new CustomException(THEATER_NOT_FOUND));

    LocalTime newStartTime = request.getStartTime();
    LocalTime newEndTime = newStartTime.plusMinutes(movie.getRunningMinute());

    if (this.scheduleRepository.existsByTheaterIdAndEndTimeGreaterThanEqual(
        theater.getId(), newStartTime)) {
      throw new CustomException(ALREADY_EXISTED_SCHEDULE);
    }

    return ScheduleDto.fromEntity(this.scheduleRepository.save(Schedule.builder()
        .movie(movie)
        .theater(theater)
        .startTime(newStartTime)
        .endTime(newEndTime)
        .build()));
  }

  @Override
  public List<ScheduleDto> getSchedulesByMovieId(Long movieId) {
    return scheduleRepository.findByMovieId(movieId).stream()
        .map(ScheduleDto::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ScheduleDto updateSchedule(Long scheduleId, UpdateScheduleDto.Request request) {
    Schedule schedule = this.scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

    Movie movie = this.movieRepository.findById(request.getMovieId())
        .orElseThrow(() -> new CustomException(MOVIE_NOT_FOUND));

    Theater theater = this.theaterRepository.findById(request.getTheaterId())
        .orElseThrow(() -> new CustomException(THEATER_NOT_FOUND));

    if (this.scheduleRepository.existsByTheaterAndStartTimeAndIdNot(
        theater, request.getStartTime(), scheduleId)) {
      throw new CustomException(ALREADY_EXISTED_SCHEDULE);
    }

    schedule.setMovie(movie);
    schedule.setTheater(theater);
    schedule.setStartTime(request.getStartTime());

    return ScheduleDto.fromEntity(this.scheduleRepository.save(schedule));
  }

  @Override
  @Transactional
  public void deleteSchedule(Long scheduleId) {
    this.scheduleRepository.delete(this.scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND)));
  }
}
