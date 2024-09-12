package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.model.dto.RegisterReservationDto;
import com.zerobase.moviereservation.model.dto.ReservationDto;
import com.zerobase.moviereservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
  public RegisterReservationDto.Response registerReservation(
      @RequestBody RegisterReservationDto.Request request
  ) {
    return RegisterReservationDto.Response.from(
        reservationService.registerReservation(request));
  }

  @PatchMapping("/cancel/{reservationId}")
  public ResponseEntity<ReservationDto> cancelReservation(
      @PathVariable("reservationId") Long reservationId,
      @RequestParam("userId") Long userId
  ) {
    return ResponseEntity.ok(
        reservationService.canceledReservation(userId, reservationId));
  }

  @GetMapping("/users/{userId}")
  public Page<ReservationDto> getAllReservation(
      @PathVariable("userId") Long userId,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "5") int size
  ) {
    return reservationService.getAllReservation(userId, page, size);
  }
}
