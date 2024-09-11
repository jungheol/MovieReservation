package com.zerobase.moviereservation.service;

import static com.zerobase.moviereservation.exception.type.ErrorCode.AUTHORIZATION_ERROR;
import static com.zerobase.moviereservation.exception.type.ErrorCode.USER_NOT_FOUND;

import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.exception.CustomException;
import com.zerobase.moviereservation.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;

  // 인증된 사용자 정보 가져오기
  public User getAuthenticatedUser(Long userId) {
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

    return authenticatedUser;
  }
}
