package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.model.dto.RegisterReservationDto;
import com.zerobase.moviereservation.model.dto.RegisterReservationDto.Response;
import com.zerobase.moviereservation.model.dto.ReservationDto;
import com.zerobase.moviereservation.service.ReservationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping
  public List<Response> registerReservations(
      @RequestBody RegisterReservationDto.Request request
  ) {
    List<ReservationDto> reservationDtos = this.reservationService.registerReservation(request);
    return reservationDtos.stream()
        .map(RegisterReservationDto.Response::from)
        .collect(Collectors.toList());
  }
}
