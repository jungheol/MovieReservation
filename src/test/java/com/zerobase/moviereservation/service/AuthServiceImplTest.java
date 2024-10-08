package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_EMAIL;
import static com.zerobase.moviereservation.exception.type.ErrorCode.PASSWORD_NOT_MATCHED;
import static com.zerobase.moviereservation.exception.type.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.Login;
import com.zerobase.moviereservation.model.dto.RegisterUserDto;
import com.zerobase.moviereservation.model.dto.UpdateUserDto;
import com.zerobase.moviereservation.model.dto.UserDto;
import com.zerobase.moviereservation.model.type.Role;
import com.zerobase.moviereservation.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
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

  @Mock
  private AuthenticationService authenticationService;

  @InjectMocks
  private AuthServiceImpl authServiceImpl;

  private RegisterUserDto.Request registerUserDto;

  private Login.Request loginDto;

  private UpdateUserDto.Request updateDto;

  private User user;

  @BeforeEach
  void setUp() {
    registerUserDto = new RegisterUserDto.Request();
    registerUserDto.setEmail("test@example.com");
    registerUserDto.setPassword("password123");
    registerUserDto.setUsername("testuser");
    registerUserDto.setBirthday(LocalDate.parse("1990-01-01"));
    registerUserDto.setPhoneNumber("123-456-7890");

    loginDto = new Login.Request();
    loginDto.setEmail("test@example.com");
    loginDto.setPassword("password123");

    updateDto = new UpdateUserDto.Request();
    updateDto.setUsername("newTestUser");
    updateDto.setPassword("newPassword123");
    updateDto.setBirthday(LocalDate.parse("1990-12-31"));
    updateDto.setPhoneNumber("010-0000-1234");

    user = User.builder()
        .email(registerUserDto.getEmail())
        .password(registerUserDto.getPassword())
        .username(registerUserDto.getUsername())
        .birthday(registerUserDto.getBirthday())
        .phoneNumber(registerUserDto.getPhoneNumber())
        .build();
  }

  @Test
  @DisplayName("유저 가입 성공")
  void testRegister_Success() {
    // given
    when(userRepository.existsByEmail(registerUserDto.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(registerUserDto.getPassword())).thenReturn("password123");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    UserDto result = authServiceImpl.registerUser(registerUserDto, Role.USER);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(registerUserDto.getEmail());
    assertThat(result.getUsername()).isEqualTo(registerUserDto.getUsername());

    // 검증
    verify(userRepository).existsByEmail(registerUserDto.getEmail());
    verify(passwordEncoder).encode(registerUserDto.getPassword());
    verify(userRepository).save(any(User.class));
  }


  @Test
  @DisplayName("동일한 이메일 존재")
  void testRegister_Fail() {
    // given
    when(userRepository.existsByEmail(registerUserDto.getEmail())).thenReturn(true);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> authServiceImpl.registerUser(registerUserDto, Role.USER));
    assertEquals(ALREADY_EXISTED_EMAIL, exception.getErrorCode());

    // 검증
    verify(userRepository).existsByEmail(registerUserDto.getEmail());
  }

  @Test
  @DisplayName("로그인 성공")
  void testLogin_Success() {
    // given
    when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);

    // when
    UserDto result = authServiceImpl.loginUser(loginDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(loginDto.getEmail());
    assertThat(result.getUsername()).isEqualTo(user.getUsername());
  }

  @Test
  @DisplayName("로그인 실패 - 비밀번호 불일치")
  void testLogin_Fail() {
    // given
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(false);

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> authServiceImpl.loginUser(loginDto));
    assertEquals(PASSWORD_NOT_MATCHED, exception.getErrorCode());

    // verify
    verify(passwordEncoder).matches(loginDto.getPassword(), user.getPassword());
  }

  @Test
  @DisplayName("유저 정보 업데이트 성공")
  void testUpdate_Success() {

    // given
    Long userId = 1L;
    User authUser = new User();
    when(authenticationService.getAuthenticatedUser(userId)).thenReturn(authUser);
    when(passwordEncoder.encode(any())).thenReturn("newPassword123");

    // when
    UserDto userDto = authServiceImpl.updateUser(userId, updateDto);

    // then
    assertEquals("newTestUser", userDto.getUsername());
    assertEquals("newPassword123", userDto.getPassword());
    assertEquals(LocalDate.parse("1990-12-31"), userDto.getBirthday());
    assertEquals("010-0000-1234", userDto.getPhoneNumber());

    // verify
    verify(authenticationService).getAuthenticatedUser(userId);
    verify(passwordEncoder).encode(updateDto.getPassword());
  }

  @Test
  @DisplayName("유저 정보 업데이트 실패")
  void testUpdate_Fail() {
    // given
    Long userId = 1L;
    when(authenticationService.getAuthenticatedUser(userId)).thenThrow(new CustomException(USER_NOT_FOUND));

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> authServiceImpl.updateUser(userId, updateDto));
    assertEquals(USER_NOT_FOUND, exception.getErrorCode());

    // verify
    verify(authenticationService).getAuthenticatedUser(userId);
  }

  @Test
  @DisplayName("유저 정보 삭제 성공")
  void testDelete_Success() {
    // given
    Long userId = 1L;
    when(authenticationService.getAuthenticatedUser(userId)).thenReturn(user);

    // when & then
    authServiceImpl.deleteUser(userId);

    // verify
    verify(authenticationService).getAuthenticatedUser(userId);
    verify(userRepository).delete(user);
  }

  @Test
  @DisplayName("유저 정보 삭제 실패")
  void testDelete_Fail() {
    // given
    Long userId = 2L;
    when(authenticationService.getAuthenticatedUser(userId)).thenThrow(new CustomException(USER_NOT_FOUND));

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> authServiceImpl.deleteUser(userId));
    assertEquals(USER_NOT_FOUND, exception.getErrorCode());

    // verify
    verify(authenticationService).getAuthenticatedUser(userId);
  }
}