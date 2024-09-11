package com.zerobase.moviereservation.repository;

import com.zerobase.moviereservation.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie.id = :movieId")
  Double findAverageRatingByMovieId(@Param("movieId") Long movieId);

  boolean existsByReservationId(Long reservationId);

  Page<Review> findAllByUserId(Long userId, Pageable pageable);
}
