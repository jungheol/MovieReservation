package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.RegisterTheaterDto;
import com.zerobase.moviereservation.model.dto.TheaterDto;
import com.zerobase.moviereservation.model.dto.UpdateTheaterDto;

public interface TheaterService {

  TheaterDto registerTheater(RegisterTheaterDto.Request request);

  TheaterDto updateTheater(Long theaterId, UpdateTheaterDto.Request request);

  void deleteTheater(Long theaterId);
}
