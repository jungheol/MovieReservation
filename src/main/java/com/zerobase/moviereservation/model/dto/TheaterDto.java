package com.zerobase.moviereservation.model.dto;

import com.zerobase.moviereservation.entity.Theater;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheaterDto {

  private Long id;
  private String theaterName;
  private String address;
  private int seatCount;

  public static TheaterDto fromEntity(Theater theater) {
    return TheaterDto.builder()
        .id(theater.getId())
        .theaterName(theater.getTheaterName())
        .address(theater.getAddress())
        .seatCount(theater.getSeatCount())
        .build();
  }
}
