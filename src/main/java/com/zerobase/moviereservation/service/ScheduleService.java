package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.RegisterScheduleDto;
import com.zerobase.moviereservation.model.dto.ScheduleDto;
import com.zerobase.moviereservation.model.dto.UpdateScheduleDto;

public interface ScheduleService {

  ScheduleDto registerSchedule(RegisterScheduleDto.Request request);

  ScheduleDto updateSchedule(Long scheduleId, UpdateScheduleDto.Request request);

  void deleteSchedule(Long scheduleId);
}
