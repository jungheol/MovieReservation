package com.zerobase.moviereservation.service;

import java.util.Map;

public interface SeatService {

  Map<String, Object> getAvailableSeats(Long scheduleId);
}
