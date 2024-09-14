package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.RESERVATION_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_ALREADY_EXIST;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_NOT_AVAILABLE;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_USER_NOT_MATCHED;

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
import com.zerobase.moviereservation.repository.ReservationRepository;
import com.zerobase.moviereservation.repository.ReviewRepository;
import jakarta.validation.Valid;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final ReservationRepository reservationRepository;
  private final ReviewRepository reviewRepository;
  private final AuthenticationService authenticationService;
  private final MovieRatingService movieRatingService;

  @Override
  @Transactional
  public ReviewDto registerReview(Long userId, Long reservationId, @Valid Request request) {
    User authuser = authenticationService.getAuthenticatedUser(userId);

    Reservation reservation = this.reservationRepository.findById(reservationId)
        .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));

    validationRegisterReview(authuser, reservation, request);

    Movie movie = reservation.getSchedule().getMovie();

    // review table 에 우선 저장
    Review review = this.reviewRepository.save(Review.builder()
        .user(authuser)
        .reservation(reservation)
        .movie(movie)
        .content(request.getContent())
        .rating(request.getRating())
        .build());

    // review table 의 정보로 avgRating 구한 후 movie table 에 저장
    movieRatingService.updateMovieRating(movie);

    return ReviewDto.fromEntity(review);
  }

  @Override
  @Transactional
  public ReviewDto updateReview(Long userId, Long reviewId, @Valid UpdateReviewDto.Request request) {
    User authuser = authenticationService.getAuthenticatedUser(userId);

    Review review = this.reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

    validationUpdateReview(authuser, review);

    if (request.getContent() != null) {
      review.setContent(request.getContent());
    }

    if (request.getRating() != null) {
      review.setRating(request.getRating());
    }

    movieRatingService.updateMovieRating(review.getMovie());

    return ReviewDto.fromEntity(this.reviewRepository.save(review));
  }

  @Override
  public Page<ReviewDto> getAllReview(Long userId, int page, int size) {
    authenticationService.getAuthenticatedUser(userId);

    Pageable pageable = PageRequest.of(page, size);

    Page<Review> reviews = reviewRepository.findAllByUserId(userId, pageable);

    if (reviews.isEmpty()) {
      throw new CustomException(REVIEW_NOT_FOUND);
    }

    return reviews.map(ReviewDto::fromEntity);
  }

  @Override
  @Transactional
  public void deleteReview(Long userId, Long reviewId) {
    authenticationService.getAuthenticatedUser(userId);

    Review review = this.reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

    Movie movie = review.getMovie();

    this.reviewRepository.delete(review);

    if (movie != null) {
      movieRatingService.updateMovieRating(movie);
    }
  }

  private void validationRegisterReview(User user, Reservation reservation,
      RegisterReviewDto.Request request) {
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
    int runningMinute = movie.getRunningMinute();

    return startTime.plusMinutes(runningMinute);
  }

  private void validationUpdateReview(User user, Review review) {
    // 리뷰 작성자와 리뷰 변경자의 userId가 다를 때
    if (!review.getUser().getId().equals(user.getId())) {
      throw new CustomException(REVIEW_USER_NOT_MATCHED);
    }
  }
}
