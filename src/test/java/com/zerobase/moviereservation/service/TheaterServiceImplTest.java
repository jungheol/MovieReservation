package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_THEATERNAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.moviereservation.entity.Theater;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterTheaterDto;
import com.zerobase.moviereservation.model.dto.TheaterDto;
import com.zerobase.moviereservation.repository.TheaterRepository;
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

  private Theater theater;

  @BeforeEach
  void setUp() {
    registerTheaterDto = new RegisterTheaterDto.Request();
    registerTheaterDto.setTheaterName("theater1");
    registerTheaterDto.setAddress("address1");
    registerTheaterDto.setSeatCount(100);

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
    when(theaterRepository.existsByTheaterName(registerTheaterDto.getTheaterName())).thenReturn(false);
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
    when(theaterRepository.existsByTheaterName(registerTheaterDto.getTheaterName())).thenReturn(true);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> theaterServiceImpl.registerTheater(registerTheaterDto));
    assertEquals(ALREADY_EXISTED_THEATERNAME, exception.getErrorCode());

    // verify
    verify(theaterRepository).existsByTheaterName(registerTheaterDto.getTheaterName());
  }
}