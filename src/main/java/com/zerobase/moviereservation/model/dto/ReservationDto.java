package com.zerobase.moviereservation.model.dto;

import com.zerobase.moviereservation.entity.Reservation;
import com.zerobase.moviereservation.entity.Seat;
import java.util.List;
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
  private String cancel;
  private String reserved;

  public static ReservationDto fromEntity(Reservation reservation) {
    Seat seat = reservation.getSeat();
    String seatInfo = seat.getRowChar() + "-" + seat.getColNum();
    return ReservationDto.builder()
        .id(reservation.getId())
        .username(reservation.getUser().getUsername())
        .movieTitle(reservation.getSchedule().getMovie().getTitle())
        .theaterName(reservation.getSchedule().getTheater().getTheaterName())
        .seats(List.of(seatInfo))
        .cancel(reservation.getCancel())
        .reserved(reservation.getReserved())
        .build();
  }
}
