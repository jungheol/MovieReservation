package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_THEATERNAME;
import static com.zerobase.moviereservation.exception.type.ErrorCode.THEATER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.moviereservation.entity.Theater;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterTheaterDto;
import com.zerobase.moviereservation.model.dto.TheaterDto;
import com.zerobase.moviereservation.model.dto.UpdateTheaterDto;
import com.zerobase.moviereservation.repository.TheaterRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TheaterServiceImplTest {

  @Mock
  private TheaterRepository theaterRepository;

  @InjectMocks
  private TheaterServiceImpl theaterServiceImpl;

  private RegisterTheaterDto.Request registerTheaterDto;

  private UpdateTheaterDto.Request updateTheaterDto;

  private Theater theater;

  @BeforeEach
  void setUp() {
    registerTheaterDto = new RegisterTheaterDto.Request();
    registerTheaterDto.setTheaterName("theater1");
    registerTheaterDto.setAddress("address1");
    registerTheaterDto.setSeatCount(100);

    updateTheaterDto = new UpdateTheaterDto.Request();
    updateTheaterDto.setTheaterName("newTheater1");
    updateTheaterDto.setAddress("newAddress");

    theater = Theater.builder()
        .theaterName(registerTheaterDto.getTheaterName())
        .address(registerTheaterDto.getAddress())
        .seatCount(registerTheaterDto.getSeatCount())
        .build();
  }

  @Test
  @DisplayName("영화관 등록 성공")
  void testRegister_Success() {
    // given
    when(theaterRepository.existsByTheaterName(registerTheaterDto.getTheaterName())).thenReturn(
        false);
    when(theaterRepository.save(any(Theater.class))).thenReturn(theater);

    // when
    TheaterDto result = theaterServiceImpl.registerTheater(registerTheaterDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getTheaterName()).isEqualTo(registerTheaterDto.getTheaterName());
    assertThat(result.getAddress()).isEqualTo(registerTheaterDto.getAddress());

    // verify
    verify(theaterRepository).existsByTheaterName(registerTheaterDto.getTheaterName());
    verify(theaterRepository).save(any(Theater.class));
  }

  @Test
  @DisplayName("영화관 등록 실패")
  void testRegister_Fail() {
    // given
    when(theaterRepository.existsByTheaterName(registerTheaterDto.getTheaterName())).thenReturn(
        true);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> theaterServiceImpl.registerTheater(registerTheaterDto));
    assertEquals(ALREADY_EXISTED_THEATERNAME, exception.getErrorCode());

    // verify
    verify(theaterRepository).existsByTheaterName(registerTheaterDto.getTheaterName());
  }

  @Test
  @DisplayName("영화관 정보 업데이트 성공")
  void testUpdate_Success() {
    // given
    Long theaterId = 1L;
    when(theaterRepository.findById(theaterId)).thenReturn(Optional.of(theater));

    // when
    TheaterDto theaterDto = theaterServiceImpl.updateTheater(theaterId, updateTheaterDto);

    // then
    assertEquals("newTheater1", theaterDto.getTheaterName());
    assertEquals("newAddress", theaterDto.getAddress());

    // verify
    verify(theaterRepository).findById(theaterId);
  }

  @Test
  @DisplayName("영화관 정보 업데이트 실패")
  void testUpdate_Fail() {
    // given
    Long theaterId = 1L;
    when(theaterRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> theaterServiceImpl.updateTheater(theaterId, updateTheaterDto));
    assertEquals(THEATER_NOT_FOUND, exception.getErrorCode());

    // verify
    verify(theaterRepository).findById(theaterId);
  }

  @Test
  @DisplayName("영화관 정보 삭제 성공")
  void testDelete_Success() {
    // given
    Long theaterId = 1L;
    when(theaterRepository.findById(theaterId)).thenReturn(Optional.of(theater));

    // when & then
    theaterServiceImpl.deleteTheater(theaterId);

    // verify
    verify(theaterRepository).findById(theaterId);
    verify(theaterRepository).delete(theater);
  }

  @Test
  @DisplayName("영화관 정보 삭제 실패")
  void testDelete_Fail() {
    // given
    Long theaterId = 1L;
    when(theaterRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> theaterServiceImpl.deleteTheater(theaterId));
    assertEquals(THEATER_NOT_FOUND, exception.getErrorCode());

    // verify
    verify(theaterRepository).findById(theaterId);
  }
}