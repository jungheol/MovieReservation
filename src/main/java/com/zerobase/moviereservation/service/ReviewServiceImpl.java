package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.AUTHORIZATION_ERROR;
import static com.zerobase.moviereservation.exception.type.ErrorCode.RESERVATION_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_ALREADY_EXIST;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_NOT_AVAILABLE;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_RATING_OUT_OF_RANGE;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_TOO_LONG;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_USER_NOT_MATCHED;
import static com.zerobase.moviereservation.exception.type.ErrorCode.USER_NOT_FOUND;

import com.zerobase.moviereservation.entity.Movie;
import com.zerobase.moviereservation.entity.Reservation;
import com.zerobase.moviereservation.entity.Review;
import com.zerobase.moviereservation.entity.Schedule;
import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterReviewDto;
import com.zerobase.moviereservation.model.dto.RegisterReviewDto.Request;
import com.zerobase.moviereservation.model.dto.ReviewDto;
import com.zerobase.moviereservation.model.dto.UpdateReviewDto;
import com.zerobase.moviereservation.model.type.CancelType;
import com.zerobase.moviereservation.repository.MovieRepository;
import com.zerobase.moviereservation.repository.ReservationRepository;
import com.zerobase.moviereservation.repository.ReviewRepository;
import com.zerobase.moviereservation.repository.UserRepository;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final UserRepository userRepository;
  private final ReservationRepository reservationRepository;
  private final ReviewRepository reviewRepository;
  private final MovieRepository movieRepository;

  @Override
  @Transactional
  public ReviewDto registerReview(Long userId, Long reservationId, Request request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getPrincipal();

    User authenticatedUser = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    // 인증된 사용자의 userId와 요청된 userId가 다를 경우 예외 발생
    if (!authenticatedUser.getId().equals(userId)) {
      throw new CustomException(AUTHORIZATION_ERROR);
    }

    User user = this.userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Reservation reservation = this.reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

    validationRegisterReview(user, reservation, request);

    Review review = this.reviewRepository.save(Review.builder()
        .user(user)
        .reservation(reservation)
        .content(request.getContent())
        .rating(request.getRating())
        .build());

    Movie movie = reservation.getSchedule().getMovie();
    Double avgRating = reviewRepository.findAverageRatingByMovieId(movie.getId());

    if (avgRating != null) {
      movie.setRating(avgRating);
      movieRepository.save(movie);
    }

    return ReviewDto.fromEntity(review);
  }

  private void validationRegisterReview(User user, Reservation reservation,
      RegisterReviewDto.Request request) {
    // 리뷰의 평점이 입력 범위를 벗어났을 때
    if (request.getRating() > 5 || request.getRating() < 0) {
      throw new CustomException(REVIEW_RATING_OUT_OF_RANGE);
    }

    // 리뷰 내용이 정해진 분량보다 더 클 때
    if (request.getContent().length() > 80) {
      throw new CustomException(REVIEW_TOO_LONG);
    }

    // 리뷰 작성자와 예약자의 userId가 다를 때
    if (!reservation.getUser().getId().equals(user.getId())) {
      throw new CustomException(REVIEW_USER_NOT_MATCHED);
    }

    // 해당 예약에 대한 리뷰가 이미 존재할 때
    if (this.reviewRepository.existsByReservationId(reservation.getId())) {
      throw new CustomException(REVIEW_ALREADY_EXIST);
    }

    // 예약이 취소되거나 해당 영화가 끝나지 않았을 때
    if (reservation.getCancel().equals(CancelType.Y)
        || LocalTime.now().isBefore(getEndTime(reservation))) {
      throw new CustomException(REVIEW_NOT_AVAILABLE);
    }
  }

  private LocalTime getEndTime(Reservation reservation) {
    Schedule schedule = reservation.getSchedule();
    Movie movie = schedule.getMovie();

    LocalTime startTime = schedule.getStartTime();
    int runningTime = movie.getRunningTime();

    return startTime.plusMinutes(runningTime);
  }

  @Override
  @Transactional
  public ReviewDto updateReview(Long userId, Long reviewId, UpdateReviewDto.Request request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getPrincipal();

    User authenticatedUser = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    // 인증된 사용자의 userId와 요청된 userId가 다를 경우 예외 발생
    if (!authenticatedUser.getId().equals(userId)) {
      throw new CustomException(AUTHORIZATION_ERROR);
    }

    User user = this.userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Review review = this.reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

    validationUpdateReview(user, review, request);

    if (request.getContent() != null) {
      review.setContent(request.getContent());
    }

    if (request.getRating() != null) {
      review.setRating(request.getRating());
    }

    Movie movie = review.getReservation().getSchedule().getMovie();
    Double avgRating = reviewRepository.findAverageRatingByMovieId(movie.getId());

    if (avgRating != null) {
      movie.setRating(avgRating);
      movieRepository.save(movie);
    }

    return ReviewDto.fromEntity(this.reviewRepository.save(review));
  }

  @Override
  public List<ReviewDto> getAllReview(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    List<Review> reviews = reviewRepository.findAllByUserId(userId);

    if (reviews.isEmpty()) {
      throw new CustomException(REVIEW_NOT_FOUND);
    }

    return reviews.stream()
        .map(ReviewDto::fromEntity)
        .collect(Collectors.toList());
  }

  private void validationUpdateReview(User user, Review review,
      UpdateReviewDto.Request request) {
    // 리뷰의 평점이 입력 범위를 벗어났을 때
    if (request.getRating() != null && (request.getRating() > 5 || request.getRating() < 0)) {
      throw new CustomException(REVIEW_RATING_OUT_OF_RANGE);
    }

    // 리뷰 내용이 정해진 분량보다 더 클 때
    if (request.getContent() != null && request.getContent().length() > 80) {
      throw new CustomException(REVIEW_TOO_LONG);
    }

    // 리뷰 작성자와 리뷰 변경자의 userId가 다를 때
    if (!review.getUser().getId().equals(user.getId())) {
      throw new CustomException(REVIEW_USER_NOT_MATCHED);
    }
  }

  @Override
  @Transactional
  public void deleteReview(Long userId, Long reviewId) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getPrincipal();

    User authenticatedUser = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    // 인증된 사용자의 userId와 요청된 userId가 다를 경우 예외 발생
    if (!authenticatedUser.getId().equals(userId)) {
      throw new CustomException(AUTHORIZATION_ERROR);
    }

    Review review = this.reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

    // 리뷰 작성자와 리뷰 삭제자의 userId가 다를 때
    if (!review.getUser().getId().equals(userId)) {
      throw new CustomException(REVIEW_USER_NOT_MATCHED);
    }

    this.reviewRepository.delete(review);

    Movie movie = review.getReservation().getSchedule().getMovie();
    Double avgRating = reviewRepository.findAverageRatingByMovieId(movie.getId());

    if (avgRating != null) {
      movie.setRating(avgRating);
      movieRepository.save(movie);
    }
  }
}
