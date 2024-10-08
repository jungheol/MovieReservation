package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_THEATERNAME;
import static com.zerobase.moviereservation.exception.type.ErrorCode.THEATER_NOT_FOUND;

import com.zerobase.moviereservation.entity.Seat;
import com.zerobase.moviereservation.entity.Theater;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterTheaterDto.Request;
import com.zerobase.moviereservation.model.dto.TheaterDto;
import com.zerobase.moviereservation.model.dto.UpdateTheaterDto;
import com.zerobase.moviereservation.repository.SeatRepository;
import com.zerobase.moviereservation.repository.TheaterRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TheaterServiceImpl implements TheaterService {

  private final TheaterRepository theaterRepository;
  private final SeatRepository seatRepository;

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

    List<Seat> seats = new ArrayList<>();
    for (char row = 'A'; row <= 'J'; row++) {
      for (int col = 1; col <= 10; col++) {
        Seat seat = new Seat();
        seat.setTheater(theater);
        seat.setRowChar(String.valueOf(row));
        seat.setColNum(col);
        seats.add(seat);
      }
    }

    seatRepository.saveAll(seats);

    return TheaterDto.fromEntity(theater);
  }

  @Override
  @Transactional
  public TheaterDto updateTheater(Long theaterId, UpdateTheaterDto.Request request) {
    Theater theater = this.theaterRepository.findById(theaterId)
        .orElseThrow(() -> new CustomException(THEATER_NOT_FOUND));

    if (this.theaterRepository.existsByTheaterName(request.getTheaterName()) &&
        !theater.getTheaterName().equals(request.getTheaterName())) {
      throw new CustomException(ALREADY_EXISTED_THEATERNAME);
    }

    theater.setTheaterName(request.getTheaterName());
    theater.setAddress(request.getAddress());

    return TheaterDto.fromEntity(theater);
  }

  @Override
  @Transactional
  public void deleteTheater(Long theaterId) {
    Theater theater = this.theaterRepository.findById(theaterId)
        .orElseThrow(() -> new CustomException(THEATER_NOT_FOUND));

    this.theaterRepository.delete(theater);
  }
}
