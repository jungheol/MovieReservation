package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.model.dto.RegisterReservationDto;
import com.zerobase.moviereservation.model.dto.ReservationDto;
import com.zerobase.moviereservation.service.ReservationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    List<RegisterReservationDto.Response> responses =
        reservationService.registerReservation(request)
            .stream().map(RegisterReservationDto.Response::from)
            .collect(Collectors.toList());
    return ResponseEntity.ok(responses);
  }

  @PatchMapping("/{reservationId}/users/{userId}/cancel")
  public ResponseEntity<ReservationDto> cancelReservation(
      @PathVariable("reservationId") Long reservationId,
      @RequestParam("userId") Long userId
  ) {
    return ResponseEntity.ok(reservationService.canceledReservation(userId, reservationId));
  }

  @GetMapping("/users/{userId}")
  public List<ReservationDto> getAllReservation(
      @PathVariable("userId") Long userId
  ) {
    return reservationService.getAllReservation(userId);
  }
}
