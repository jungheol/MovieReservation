package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.model.dto.RegisterReviewDto;
import com.zerobase.moviereservation.model.dto.ReviewDto;
import com.zerobase.moviereservation.model.dto.UpdateReviewDto;
import com.zerobase.moviereservation.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  public RegisterReviewDto.Response registerReview(
      @RequestParam("userId") Long userId,
      @RequestParam("reservationId") Long reservationId,
      @RequestBody RegisterReviewDto.Request request
  ) {
    return RegisterReviewDto.Response.from(
        this.reviewService.registerReview(userId, reservationId, request));
  }

  @PatchMapping("/{reviewId}")
  public UpdateReviewDto.Response updateReview(
      @RequestParam("userId") Long userId,
      @PathVariable("reviewId") Long reviewId,
      @RequestBody UpdateReviewDto.Request request
  ) {
    return UpdateReviewDto.Response.from(
        this.reviewService.updateReview(userId, reviewId, request));
  }

  @GetMapping("/users/{userId}")
  public Page<ReviewDto> getAllReview(
      @PathVariable("userId") Long userId,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "5") int size
  ) {
    return reviewService.getAllReview(userId, page, size);
  }

  @DeleteMapping("/{reviewId}")
  public ResponseEntity<?> deleteReview(
      @RequestParam("userId") Long userId,
      @PathVariable("reviewId") Long reviewId
  ) {
    this.reviewService.deleteReview(userId, reviewId);
    return ResponseEntity.ok("해당 리뷰를 삭제하였습니다.");
  }
}
