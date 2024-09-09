package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.model.dto.RegisterScheduleDto;
import com.zerobase.moviereservation.model.dto.ScheduleDto;
import com.zerobase.moviereservation.model.dto.UpdateScheduleDto;
import com.zerobase.moviereservation.service.ScheduleService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

  private final ScheduleService scheduleService;

  @PostMapping
  public RegisterScheduleDto.Response register(@RequestBody RegisterScheduleDto.Request request) {
    return RegisterScheduleDto.Response.from(this.scheduleService.registerSchedule(request));
  }

  @GetMapping("/by-movie/{movieId}")
  public List<ScheduleDto> getSchedulesByMovieId(
      @PathVariable("movieId") Long movieId
  ) {
    return scheduleService.getSchedulesByMovieId(movieId);
  }

  @PutMapping("/{scheduleId}")
  public UpdateScheduleDto.Response updateSchedule(
      @PathVariable("scheduleId") Long scheduleId,
      @RequestBody @Valid UpdateScheduleDto.Request request
  ) {
    return UpdateScheduleDto.Response.from(this.scheduleService.updateSchedule(scheduleId, request));
  }

  @DeleteMapping("/{scheduleId}")
  public ResponseEntity<?> deleteSchedule(
      @PathVariable("scheduleId") Long scheduleId
  ) {
    this.scheduleService.deleteSchedule(scheduleId);
    return ResponseEntity.ok("해당 스케쥴을 삭제했습니다.");
  }

}
