package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.ALREADY_EXISTED_EMAIL;
import static com.zerobase.moviereservation.exception.type.ErrorCode.PASSWORD_NOT_MATCHED;
import static com.zerobase.moviereservation.exception.type.ErrorCode.USER_NOT_FOUND;
import static com.zerobase.moviereservation.model.type.Role.USER;

import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.model.dto.Login;
import com.zerobase.moviereservation.model.dto.RegisterUser;
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
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserDto register(RegisterUser.Request request) {
    boolean exists = this.userRepository.existsByEmail(request.getEmail());

    if (exists) {
      throw new CustomException(ALREADY_EXISTED_EMAIL);
    }

    request.setPassword(this.passwordEncoder.encode(request.getPassword()));

    User user = this.userRepository.save(User.builder()
        .email(request.getEmail())
        .password(request.getPassword())
        .username(request.getUsername())
        .birthday(request.getBirthday())
        .phoneNumber(request.getPhoneNumber())
        .role(USER)
        .build());

    return UserDto.fromEntity(user);
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    if (this.userRepository.existsByEmail(email)) {
      User user = checkEmail(email);

      return createUserDetail(user.getEmail(), user.getPassword(), USER);
    }

    throw new UsernameNotFoundException("USER not found with email: " + email);
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
