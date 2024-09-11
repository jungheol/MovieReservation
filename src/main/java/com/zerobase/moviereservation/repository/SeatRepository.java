package com.zerobase.moviereservation.repository;

import com.zerobase.moviereservation.entity.Seat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

  List<Seat> findByTheaterId(Long theaterId);
}
