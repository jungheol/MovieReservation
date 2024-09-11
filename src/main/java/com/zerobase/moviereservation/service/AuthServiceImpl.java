package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_EMAIL;
import static com.zerobase.moviereservation.exception.type.ErrorCode.PASSWORD_NOT_MATCHED;
import static com.zerobase.moviereservation.exception.type.ErrorCode.USER_NOT_FOUND;

import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.Login;
import com.zerobase.moviereservation.model.dto.RegisterUserDto;
import com.zerobase.moviereservation.model.dto.UpdateUserDto;
import com.zerobase.moviereservation.model.dto.UserDto;
import com.zerobase.moviereservation.model.type.Role;
import com.zerobase.moviereservation.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {

  private final UserRepository userRepository;
  private final AuthenticationService authenticationService;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserDto registerUser(RegisterUserDto.Request request, Role role) {
    if (this.userRepository.existsByEmail(request.getEmail())) {
      throw new CustomException(ALREADY_EXISTED_EMAIL);
    }

    request.setPassword(this.passwordEncoder.encode(request.getPassword()));

    User user = this.userRepository.save(User.builder()
        .email(request.getEmail())
        .password(request.getPassword())
        .username(request.getUsername())
        .birthday(request.getBirthday())
        .phoneNumber(request.getPhoneNumber())
        .role(role)
        .build());

    return UserDto.fromEntity(user);
  }

  public UserDto loginUser(Login.Request request) {
    UserDto userDto = UserDto.fromEntity(checkEmail(request.getEmail()));

    if (!this.passwordEncoder.matches(request.getPassword(), userDto.getPassword())) {
      throw new CustomException(PASSWORD_NOT_MATCHED);
    }

    return userDto;
  }

  @Override
  @Transactional
  public UserDto updateUser(Long userId, UpdateUserDto.Request request) {
    User authUser = authenticationService.getAuthenticatedUser(userId);

    request.setPassword(this.passwordEncoder.encode(request.getPassword()));

    authUser.setPassword(request.getPassword());
    authUser.setUsername(request.getUsername());
    authUser.setBirthday(request.getBirthday());
    authUser.setPhoneNumber(request.getPhoneNumber());

    return UserDto.fromEntity(authUser);
  }

  @Override
  @Transactional
  public void deleteUser(Long userId) {
    User authUser = authenticationService.getAuthenticatedUser(userId);

    this.userRepository.delete(authUser);
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email).orElseThrow(() -> new
        UsernameNotFoundException("USER not found with email: " + email));

    return createUserDetail(user.getEmail(), user.getPassword(), user.getRole());
  }

  private User checkEmail(String email) {
    return this.userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }

  private UserDetails createUserDetail(String email, String password, Role role) {
    return org.springframework.security.core.userdetails.User.withUsername(email)
        .password(this.passwordEncoder.encode(password))
        .roles(String.valueOf(role))
        .build();
  }
}
