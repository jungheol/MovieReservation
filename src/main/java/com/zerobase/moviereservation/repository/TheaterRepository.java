package com.zerobase.moviereservation.repository;

import com.zerobase.moviereservation.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {

  boolean existsByTheaterName(String theaterName);
}
