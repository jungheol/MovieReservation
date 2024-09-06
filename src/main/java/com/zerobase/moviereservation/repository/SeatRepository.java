package com.zerobase.moviereservation.repository;

import com.zerobase.moviereservation.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {

}
