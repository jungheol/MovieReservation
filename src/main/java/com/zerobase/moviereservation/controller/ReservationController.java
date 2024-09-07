package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.model.dto.RegisterReservationDto;
import com.zerobase.moviereservation.model.dto.ReservationDto;
import com.zerobase.moviereservation.service.ReservationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping
  public ResponseEntity<List<RegisterReservationDto.Response>> registerReservation(
      @RequestBody RegisterReservationDto.Request request) {
    List<RegisterReservationDto.Response> responses = reservationService.registerReservation(request)
        .stream().map(RegisterReservationDto.Response::from)
        .collect(Collectors.toList());
    return ResponseEntity.ok(responses);
  }

  @PutMapping("/cancel")
  public ResponseEntity<List<ReservationDto>> cancelReservations(
      @RequestParam("userId") Long userId,
      @RequestParam("scheduleId") Long scheduleId
  ) {
    return ResponseEntity.ok(reservationService.canceledReservation(userId, scheduleId));
  }
}
