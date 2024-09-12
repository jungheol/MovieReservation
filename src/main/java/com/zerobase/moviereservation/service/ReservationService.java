package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.RegisterReservationDto;
import com.zerobase.moviereservation.model.dto.ReservationDto;
import org.springframework.data.domain.Page;

public interface ReservationService {

  ReservationDto registerReservation(RegisterReservationDto.Request request);

  ReservationDto canceledReservation(Long userId, Long reservationId);

  Page<ReservationDto> getAllReservation(Long userId, int page, int size);
}
