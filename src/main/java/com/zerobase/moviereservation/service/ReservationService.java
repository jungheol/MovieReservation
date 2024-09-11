package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.RegisterReservationDto;
import com.zerobase.moviereservation.model.dto.ReservationDto;
import java.util.List;

public interface ReservationService {

  List<ReservationDto> registerReservation(RegisterReservationDto.Request request);

  ReservationDto canceledReservation(Long userId, Long reservationId);

  List<ReservationDto> getAllReservation(Long userId);
}
