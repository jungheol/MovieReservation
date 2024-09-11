package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.RegisterReviewDto;
import com.zerobase.moviereservation.model.dto.ReviewDto;
import com.zerobase.moviereservation.model.dto.UpdateReviewDto;
import java.util.List;

public interface ReviewService {

  ReviewDto registerReview(Long userId, Long reservationId, RegisterReviewDto.Request request);

  ReviewDto updateReview(Long userId, Long reviewId, UpdateReviewDto.Request request);

  public List<ReviewDto> getAllReview(Long userId);

  void deleteReview(Long userId, Long reviewId);
}
