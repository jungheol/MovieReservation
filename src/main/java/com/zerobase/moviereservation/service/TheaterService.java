package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.RegisterTheaterDto;
import com.zerobase.moviereservation.model.dto.TheaterDto;

public interface TheaterService {

  TheaterDto registerTheater(RegisterTheaterDto.Request request);
}
