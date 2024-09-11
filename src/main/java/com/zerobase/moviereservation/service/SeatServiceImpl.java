package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.entity.Schedule;
import com.zerobase.moviereservation.entity.Seat;
import com.zerobase.moviereservation.repository.ReservationRepository;
import com.zerobase.moviereservation.repository.ScheduleRepository;
import com.zerobase.moviereservation.repository.SeatRepository;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SeatServiceImpl implements SeatService {

  private final ReservationRepository reservationRepository;
  private final SeatRepository seatRepository;
  private final ScheduleRepository scheduleRepository;

  @Override
  public Map<String, Object> getAvailableSeats(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new RuntimeException("Schedule not found"));

    // 해당 스케줄의 영화관의 모든 좌석을 조회
    List<Seat> allSeats = seatRepository.findByTheaterId(schedule.getTheater().getId());

    // 예약된 좌석을 조회
    List<Seat> reservedSeats = reservationRepository.findReservedSeatsByScheduleId(scheduleId);

    // 예약되지 않은 좌석을 필터링하고 정렬
    List<String> availableSeats = allSeats.stream()
        .filter(seat -> !reservedSeats.contains(seat))  // 예약되지 않은 좌석 필터링
        .sorted(Comparator.comparing(Seat::getRowChar).thenComparing(Seat::getColNum)) // 정렬
        .map(seat -> seat.getRowChar() + "-" + seat.getColNum())  // "A-1" 형식으로 변환
        .collect(Collectors.toList());

    // 반환할 데이터 구조 생성
    Map<String, Object> seatMap = new HashMap<>();
    seatMap.put("scheduleId", scheduleId);
    seatMap.put("seatList", availableSeats);

    return seatMap;
  }
}
