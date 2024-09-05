package com.zerobase.moviereservation.repository;

import com.zerobase.moviereservation.entity.Movie;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

  Optional<Movie> findByMovieTitle(String movieTitle);

  boolean existsByMovieTitle(String movieTitle);
}
