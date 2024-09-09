package com.zerobase.moviereservation.model.dto;

import com.zerobase.moviereservation.entity.Reservation;
import com.zerobase.moviereservation.model.type.CancelType;
import com.zerobase.moviereservation.model.type.ReservedType;
import java.util.List;
import java.util.stream.Collectors;
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
public class ReservationDto {

  private Long id;
  private String username;
  private String movieTitle;
  private String theaterName;
  private List<String> seats;
  private CancelType cancel;
  private ReservedType reserved;

  public static ReservationDto fromEntity(Reservation reservation) {
    List<String> seatInfoList = reservation.getSeats().stream()
        .map(seat -> seat.getRowChar() + "-" + seat.getColNum())
        .collect(Collectors.toList());

    return ReservationDto.builder()
        .id(reservation.getId())
        .username(reservation.getUser().getUsername())
        .movieTitle(reservation.getSchedule().getMovie().getTitle())
        .theaterName(reservation.getSchedule().getTheater().getTheaterName())
        .seats(seatInfoList)
        .cancel(reservation.getCancel())
        .reserved(reservation.getReserved())
        .build();
  }
}
