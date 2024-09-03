package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_THEATERNAME;

import com.zerobase.moviereservation.entity.Theater;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterTheaterDto.Request;
import com.zerobase.moviereservation.model.dto.TheaterDto;
import com.zerobase.moviereservation.repository.TheaterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TheaterServiceImpl implements TheaterService {

  private final TheaterRepository theaterRepository;

  @Override
  @Transactional
  public TheaterDto registerTheater(Request request) {
    if (this.theaterRepository.existsByTheaterName(request.getTheaterName())) {
      throw new CustomException(ALREADY_EXISTED_THEATERNAME);
    }

    Theater theater = this.theaterRepository.save(Theater.builder()
        .theaterName(request.getTheaterName())
        .address(request.getAddress())
        .seatCount(request.getSeatCount())
        .build());

    return TheaterDto.fromEntity(theater);
  }
}
