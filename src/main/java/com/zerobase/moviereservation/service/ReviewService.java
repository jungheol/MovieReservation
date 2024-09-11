package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.RegisterReviewDto;
import com.zerobase.moviereservation.model.dto.ReviewDto;
import com.zerobase.moviereservation.model.dto.UpdateReviewDto;
import org.springframework.data.domain.Page;

public interface ReviewService {

  ReviewDto registerReview(Long userId, Long reservationId, RegisterReviewDto.Request request);

  ReviewDto updateReview(Long userId, Long reviewId, UpdateReviewDto.Request request);

  Page<ReviewDto> getAllReview(Long userId, int page, int size);

  void deleteReview(Long userId, Long reviewId);
}
