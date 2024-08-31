package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.RegisterUserDto;
import com.zerobase.moviereservation.model.dto.UserDto;
import com.zerobase.moviereservation.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthServiceImpl authServiceImpl;

  private RegisterUserDto.Request req;

  @BeforeEach
  void setUp() {
    req = new RegisterUserDto.Request();
    req.setEmail("test@example.com");
    req.setPassword("password123");
    req.setUsername("testuser");
    req.setBirthday(LocalDate.parse("1990-01-01"));
    req.setPhoneNumber("123-456-7890");
  }

  @Test
  @DisplayName("유저 가입 성공")
  void testRegister_Success() {
    // given
    when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(req.getPassword())).thenReturn("password123");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    UserDto result = authServiceImpl.register(req);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(req.getEmail());
    assertThat(result.getUsername()).isEqualTo(req.getUsername());

    // 검증
    verify(userRepository).existsByEmail(req.getEmail());
    verify(passwordEncoder).encode(req.getPassword());
    verify(userRepository).save(any(User.class));
  }


  @Test
  @DisplayName("동일한 이메일 존재")
  void testRegister_Fail() {
    // given
    when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> authServiceImpl.register(req));
    assertEquals(ALREADY_EXISTED_EMAIL, exception.getErrorCode());

    // 검증
    verify(userRepository).existsByEmail(req.getEmail());
  }
}