package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_ALREADY_EXIST;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_NOT_AVAILABLE;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.REVIEW_USER_NOT_MATCHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.moviereservation.entity.Movie;
import com.zerobase.moviereservation.entity.Reservation;
import com.zerobase.moviereservation.entity.Review;
import com.zerobase.moviereservation.entity.Schedule;
import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterReviewDto;
import com.zerobase.moviereservation.model.dto.ReviewDto;
import com.zerobase.moviereservation.model.dto.UpdateReviewDto;
import com.zerobase.moviereservation.model.type.CancelType;
import com.zerobase.moviereservation.repository.ReservationRepository;
import com.zerobase.moviereservation.repository.ReviewRepository;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private AuthenticationService authenticationService;

  @Mock
  private MovieRatingService movieRatingService;

  @InjectMocks
  private ReviewServiceImpl reviewServiceImpl;

  private RegisterReviewDto.Request registerReviewDto;
  private UpdateReviewDto.Request updateReviewDto;
  private User user;
  private Movie movie;
  private Reservation reservation;
  private Review review;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);

    movie = new Movie();
    movie.setId(1L);
    movie.setTitle("Sample Movie");
    movie.setRunningMinute(120);

    Schedule schedule = new Schedule();
    schedule.setMovie(movie);
    schedule.setStartTime(LocalTime.of(12, 0));

    reservation = new Reservation();
    reservation.setId(1L);
    reservation.setUser(user);
    reservation.setSchedule(schedule);
    reservation.setCancel(CancelType.N);

    registerReviewDto = new RegisterReviewDto.Request();
    registerReviewDto.setContent("좋은 영화였어요!");
    registerReviewDto.setRating(5);

    updateReviewDto = new UpdateReviewDto.Request();
    updateReviewDto.setContent("생각해보니 조금 그랬어요!");
    updateReviewDto.setRating(4);

    review = Review.builder()
        .user(user)
        .movie(movie)
        .reservation(reservation)
        .content(registerReviewDto.getContent())
        .rating(registerReviewDto.getRating())
        .build();
  }

  @Test
  @DisplayName("리뷰 등록 성공")
  void testRegister_Success() {
    // given
    when(authenticationService.getAuthenticatedUser(user.getId())).thenReturn(user);
    when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
    when(reviewRepository.existsByReservationId(reservation.getId())).thenReturn(false);
    when(reviewRepository.save(any(Review.class))).thenReturn(review);

    // when
    ReviewDto result = reviewServiceImpl.registerReview(user.getId(), reservation.getId(), registerReviewDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEqualTo(registerReviewDto.getContent());
    assertThat(result.getRating()).isEqualTo(registerReviewDto.getRating());

    // verify
    verify(authenticationService).getAuthenticatedUser(user.getId());
    verify(reservationRepository).findById(reservation.getId());
    verify(reviewRepository).existsByReservationId(reservation.getId());
    verify(movieRatingService).updateMovieRating(movie);
  }

  @Test
  @DisplayName("리뷰 등록 실패 - 이미 존재하는 리뷰일 때")
  void testRegister_Fail_ReviewAlreadyExist() {
    // given
    when(authenticationService.getAuthenticatedUser(anyLong())).thenReturn(user);
    when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
    when(reviewRepository.existsByReservationId(reservation.getId())).thenReturn(true);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> reviewServiceImpl.registerReview(user.getId(), reservation.getId(), registerReviewDto));
    assertEquals(REVIEW_ALREADY_EXIST, exception.getErrorCode());

    // verify
    verify(authenticationService).getAuthenticatedUser(user.getId());
  }

  @Test
  @DisplayName("리뷰 등록 실패 - 리뷰 작성자와 예약자가 다를 경우")
  void testRegister_Fail_UserNotMatched() {
    // given
    User differentUser = new User();
    differentUser.setId(2L);

    when(authenticationService.getAuthenticatedUser(user.getId())).thenReturn(user);
    when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
    reservation.setUser(differentUser);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> reviewServiceImpl.registerReview(user.getId(), reservation.getId(), registerReviewDto));
    assertEquals(REVIEW_USER_NOT_MATCHED, exception.getErrorCode());

    // verify
    verify(authenticationService).getAuthenticatedUser(user.getId());
    verify(reservationRepository).findById(reservation.getId());
  }

  @Test
  @DisplayName("리뷰 등록 실패 - 예약이 취소된 경우")
  void testRegister_Fail_ReservationCancelled() {
    // given
    when(authenticationService.getAuthenticatedUser(anyLong())).thenReturn(user);
    when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
    reservation.setCancel(CancelType.Y);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> reviewServiceImpl.registerReview(user.getId(), reservation.getId(), registerReviewDto));
    assertEquals(REVIEW_NOT_AVAILABLE, exception.getErrorCode());

    // verify
    verify(authenticationService).getAuthenticatedUser(user.getId());
    verify(reservationRepository).findById(reservation.getId());
  }

  @Test
  @DisplayName("리뷰 등록 실패 - 영화 상영이 끝나지 않은 경우")
  void testRegister_Fail_MovieNotEnded() {
    // given
    when(authenticationService.getAuthenticatedUser(anyLong())).thenReturn(user);
    when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

    // 영화 상영 시간이 아직 끝나지 않은 상태로 설정
    Schedule schedule = reservation.getSchedule();
    schedule.setStartTime(LocalTime.now().plusHours(1)); // 현재 시간보다 1시간 후에 영화 시작

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> reviewServiceImpl.registerReview(user.getId(), reservation.getId(), registerReviewDto));
    assertEquals(REVIEW_NOT_AVAILABLE, exception.getErrorCode());

    // verify
    verify(authenticationService).getAuthenticatedUser(user.getId());
    verify(reservationRepository).findById(reservation.getId());
  }

  @Test
  @DisplayName("리뷰 업데이트 성공")
  void testUpdate_Success() {
    // given
    when(authenticationService.getAuthenticatedUser(user.getId())).thenReturn(user);
    when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
    when(reviewRepository.save(any(Review.class))).thenReturn(review);

    // when
    ReviewDto result = reviewServiceImpl.updateReview(user.getId(), review.getId(), updateReviewDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEqualTo(updateReviewDto.getContent());
    assertThat(result.getRating()).isEqualTo(updateReviewDto.getRating());

    // verify
    verify(authenticationService).getAuthenticatedUser(user.getId());
    verify(movieRatingService).updateMovieRating(movie);
  }

  @Test
  @DisplayName("리뷰 업데이트 실패 - 리뷰 작성자와 변경자가 다를 경우")
  void testUpdate_Fail_UserNotMatched() {
    // given
    User differentUser = new User();
    differentUser.setId(2L);

    when(authenticationService.getAuthenticatedUser(user.getId())).thenReturn(user);
    when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
    review.setUser(differentUser);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> reviewServiceImpl.updateReview(user.getId(), review.getId(), updateReviewDto));
    assertEquals(REVIEW_USER_NOT_MATCHED, exception.getErrorCode());

    // verify
    verify(authenticationService).getAuthenticatedUser(user.getId());
    verify(reviewRepository).findById(review.getId());
  }

  @Test
  @DisplayName("리뷰 삭제 성공")
  void testDelete_Success() {
    // given
    when(authenticationService.getAuthenticatedUser(user.getId())).thenReturn(user);
    when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

    // when
    reviewServiceImpl.deleteReview(user.getId(), review.getId());

    // then
    verify(authenticationService).getAuthenticatedUser(user.getId());
    verify(reviewRepository).findById(review.getId());
    verify(reviewRepository).delete(review);
    verify(movieRatingService).updateMovieRating(movie);
  }

  @Test
  @DisplayName("리뷰 삭제 실패 - 리뷰를 찾을 수 없음")
  void testDelete_Fail_ReviewNotFound() {
    // given
    when(authenticationService.getAuthenticatedUser(anyLong())).thenReturn(user);
    when(reviewRepository.findById(review.getId())).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> reviewServiceImpl.deleteReview(user.getId(), review.getId()));
    assertEquals(REVIEW_NOT_FOUND, exception.getErrorCode());

    // then
    verify(authenticationService).getAuthenticatedUser(anyLong());
    verify(reviewRepository).findById(review.getId());
  }

  @Test
  @DisplayName("리뷰 목록 가져오기 성공")
  void testGetAll_Success() {
    // given
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Review> reviewPage = new PageImpl<>(Collections.singletonList(review));
    when(authenticationService.getAuthenticatedUser(user.getId())).thenReturn(user);
    when(reviewRepository.findAllByUserId(user.getId(), pageable))
        .thenReturn(reviewPage);

    // when
    Page<ReviewDto> result = reviewServiceImpl.getAllReview(user.getId(), 0, 10);

    // then
    assertThat(result).isNotEmpty();
    assertThat(result.getContent().get(0).getId()).isEqualTo(review.getId());

    // verify
    verify(authenticationService).getAuthenticatedUser(user.getId());
    verify(reviewRepository).findAllByUserId(user.getId(), pageable);
  }

  @Test
  @DisplayName("리뷰 목록 가져오기 실패 - 리뷰를 찾을 수 없음")
  void testGetAll_Fail_ReviewNotFound() {
    // given
    PageRequest pageable = PageRequest.of(0, 10);
    when(authenticationService.getAuthenticatedUser(anyLong())).thenReturn(user);
    when(reviewRepository.findAllByUserId(anyLong(), any(PageRequest.class)))
        .thenReturn(Page.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> reviewServiceImpl.getAllReview(user.getId(), 0, 10));
    assertThat(exception.getErrorCode()).isEqualTo(REVIEW_NOT_FOUND);

    // verify
    verify(authenticationService).getAuthenticatedUser(anyLong());
    verify(reviewRepository).findAllByUserId(anyLong(), eq(pageable));
  }
}