package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_RESERVATION;
import static com.zerobase.moviereservation.exception.type.ErrorCode.RESERVATION_NOT_FOUND;
import static com.zerobase.moviereservation.exception.type.ErrorCode.SEAT_NOT_VALID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.zerobase.moviereservation.entity.*;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterReservationDto;
import com.zerobase.moviereservation.model.dto.ReservationDto;
import com.zerobase.moviereservation.model.type.CancelType;
import com.zerobase.moviereservation.model.type.PaymentType;
import com.zerobase.moviereservation.model.type.ReservedType;
import com.zerobase.moviereservation.repository.ReservationRepository;
import com.zerobase.moviereservation.repository.ScheduleRepository;
import com.zerobase.moviereservation.repository.SeatRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private ScheduleRepository scheduleRepository;

  @Mock
  private SeatRepository seatRepository;

  @Mock
  private PaymentService paymentService;

  @Mock
  private AuthenticationService authenticationService;

  @InjectMocks
  private ReservationServiceImpl reservationServiceImpl;

  private RegisterReservationDto.Request registerReservationDto;
  private Reservation reservation;
  private Schedule schedule;
  private Theater theater;
  private Movie movie;
  private Seat seat;
  private User user;
  private Payment payment;

  @BeforeEach
  void setUp() {

    movie = new Movie();
    movie.setId(1L);
    movie.setTitle("Sample Movie");
    movie.setDirector("Sample Director");
    movie.setGenre("Action");
    movie.setRunningMinute(140);
    movie.setReleaseDate(LocalDate.parse("2024-09-01"));

    theater = new Theater();
    theater.setId(1L);
    theater.setTheaterName("Sample Theater");

    user = new User();
    user.setId(1L);

    schedule = new Schedule();
    schedule.setId(1L);
    schedule.setMovie(movie);
    schedule.setTheater(theater);

    seat = new Seat();
    seat.setId(1L);

    registerReservationDto = new RegisterReservationDto.Request();
    registerReservationDto.setUserId(user.getId());
    registerReservationDto.setScheduleId(schedule.getId());
    registerReservationDto.setSeatIds(Collections.singletonList(seat.getId()));

    reservation = Reservation.builder()
        .user(user)
        .schedule(schedule)
        .seats(Collections.singletonList(seat))
        .cancel(CancelType.N)
        .reserved(ReservedType.Y)
        .build();

    payment = new Payment();
    payment.setStatus(PaymentType.S);
  }

  @Test
  @DisplayName("예약 등록 성공")
  void testRegisterReservation_Success() {
    List<Long> seatIds = List.of(1L);
    List<String> seatInfoList = reservation.getSeats().stream()
        .map(seat -> seat.getRowChar() + "-" + seat.getColNum())
        .collect(Collectors.toList());
    User authUser = new User();
    // given
    when(authenticationService.getAuthenticatedUser(user.getId())).thenReturn(authUser);
    when(scheduleRepository.findById(schedule.getId())).thenReturn(Optional.of(schedule));
    when(reservationRepository.findByScheduleIdAndSeatsIdIn(schedule.getId(), seatIds))
        .thenReturn(List.of());
    when(seatRepository.findAllById(seatIds)).thenReturn(List.of(seat));
    when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
    when(paymentService.processPayment(any(Reservation.class), anyInt())).thenReturn(payment);

    // when
    ReservationDto result = reservationServiceImpl.registerReservation(registerReservationDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo(user.getUsername());
    assertThat(result.getMovieTitle()).isEqualTo(schedule.getMovie().getTitle());
    assertThat(result.getSeats()).isEqualTo(seatInfoList);

    // verify
    verify(authenticationService).getAuthenticatedUser(user.getId());
    verify(scheduleRepository).findById(schedule.getId());
    verify(reservationRepository).findByScheduleIdAndSeatsIdIn(schedule.getId(), seatIds);
    verify(seatRepository).findAllById(seatIds);
    verify(reservationRepository).save(any(Reservation.class));
    verify(paymentService).processPayment(any(Reservation.class), anyInt());
  }

  @Test
  @DisplayName("예약 등록 실패 - 이미 예약된 좌석")
  void testRegisterReservation_Failure_AlreadyExistedReservation() {
    // given
    when(authenticationService.getAuthenticatedUser(anyLong())).thenReturn(user);
    when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
    when(reservationRepository.findByScheduleIdAndSeatsIdIn(anyLong(), any()))
        .thenReturn(Collections.singletonList(reservation)); // 예약이 존재하는 경우

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> reservationServiceImpl.registerReservation(registerReservationDto));
    assertThat(exception.getErrorCode()).isEqualTo(ALREADY_EXISTED_RESERVATION);

    // verify
    verify(authenticationService).getAuthenticatedUser(anyLong());
    verify(scheduleRepository).findById(anyLong());
    verify(reservationRepository).findByScheduleIdAndSeatsIdIn(anyLong(), any());
    verify(seatRepository, never()).findAllById(any());
  }

  @Test
  @DisplayName("예약 등록 실패 - 좌석 유효하지 않음")
  void testRegisterReservation_Failure_SeatNotValid() {
    // given
    when(authenticationService.getAuthenticatedUser(anyLong())).thenReturn(user);
    when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
    when(reservationRepository.findByScheduleIdAndSeatsIdIn(anyLong(), any()))
        .thenReturn(Collections.emptyList());
    when(seatRepository.findAllById(any())).thenReturn(Collections.emptyList()); // 유효하지 않은 좌석

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> reservationServiceImpl.registerReservation(registerReservationDto));
    assertThat(exception.getErrorCode()).isEqualTo(SEAT_NOT_VALID);

    // verify
    verify(authenticationService).getAuthenticatedUser(anyLong());
    verify(scheduleRepository).findById(anyLong());
    verify(reservationRepository).findByScheduleIdAndSeatsIdIn(anyLong(), any());
    verify(seatRepository).findAllById(any());
  }

  @Test
  @DisplayName("예약 취소 성공")
  void testCanceledReservation_Success() {
    // given
    Long reservationId = 1L;
    when(authenticationService.getAuthenticatedUser(user.getId())).thenReturn(user);
    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
    when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

    // when
    ReservationDto result = reservationServiceImpl.canceledReservation(user.getId(), reservationId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getCancel()).isEqualTo(CancelType.Y);

    // verify
    verify(authenticationService).getAuthenticatedUser(user.getId());
    verify(reservationRepository).findById(reservationId);
    verify(reservationRepository).save(any(Reservation.class));
  }

  @Test
  @DisplayName("예약 취소 실패 - 예약 없음")
  void testCanceledReservation_Failure_ReservationNotFound() {
    // given
    Long reservationId = 1L;
    when(authenticationService.getAuthenticatedUser(anyLong())).thenReturn(user);
    when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> reservationServiceImpl.canceledReservation(user.getId(), reservationId));
    assertThat(exception.getErrorCode()).isEqualTo(RESERVATION_NOT_FOUND);

    // verify
    verify(authenticationService).getAuthenticatedUser(anyLong());
    verify(reservationRepository).findById(anyLong());
    verify(reservationRepository, never()).save(any(Reservation.class));
  }

  @Test
  @DisplayName("예약 조회 성공")
  void testGetAllReservation_Success() {
    // given
    int page = 0;
    int size = 10;
    Page<Reservation> reservationPage = new PageImpl<>(Collections.singletonList(reservation));
    when(authenticationService.getAuthenticatedUser(user.getId())).thenReturn(user);
    when(reservationRepository.findByUserId(eq(user.getId()), any(Pageable.class))).thenReturn(reservationPage);

    // when
    Page<ReservationDto> result = reservationServiceImpl.getAllReservation(user.getId(), page, size);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent()).hasSize(1);

    // verify
    verify(authenticationService).getAuthenticatedUser(user.getId());
    verify(reservationRepository).findByUserId(user.getId(), PageRequest.of(page, size));
  }

  @Test
  @DisplayName("예약 조회 실패 - 예약 없음")
  void testGetAllReservation_Failure_NoReservations() {
    // given
    int page = 0;
    int size = 10;
    when(authenticationService.getAuthenticatedUser(anyLong())).thenReturn(user);
    when(reservationRepository.findByUserId(anyLong(), any(Pageable.class)))
        .thenReturn(Page.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> reservationServiceImpl.getAllReservation(user.getId(), page, size));
    assertThat(exception.getErrorCode()).isEqualTo(RESERVATION_NOT_FOUND);

    // verify
    verify(authenticationService).getAuthenticatedUser(anyLong());
    verify(reservationRepository).findByUserId(anyLong(), eq(PageRequest.of(page, size)));
  }
}
