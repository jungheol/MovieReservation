package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.RegisterScheduleDto;
import com.zerobase.moviereservation.model.dto.ScheduleDto;
import com.zerobase.moviereservation.model.dto.UpdateScheduleDto;
import java.util.List;

public interface ScheduleService {

  ScheduleDto registerSchedule(RegisterScheduleDto.Request request);

  List<ScheduleDto> getSchedulesByMovieId(Long movieId);

  ScheduleDto updateSchedule(Long scheduleId, UpdateScheduleDto.Request request);

  void deleteSchedule(Long scheduleId);
}
