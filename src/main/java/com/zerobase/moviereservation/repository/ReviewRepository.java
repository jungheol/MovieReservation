package com.zerobase.moviereservation.repository;

import com.zerobase.moviereservation.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reservation.schedule.movie.id = :movieId")
  Double findAverageRatingByMovieId(@Param("movieId") Long movieId);

  boolean existsByReservationId(Long reservationId);

  List<Review> findAllByUserId(Long userId);
}
