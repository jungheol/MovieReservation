package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.service.SeatService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
public class SeatController {

  private final SeatService seatService;

  @GetMapping
  public Map<String, Object> getAvailableSeats(
      @RequestParam("scheduleId") Long scheduleId
  ) {
    return seatService.getAvailableSeats(scheduleId);
  }
}
