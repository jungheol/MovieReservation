package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.Login;
import com.zerobase.moviereservation.model.dto.RegisterUserDto;
import com.zerobase.moviereservation.model.dto.UpdateUserDto;
import com.zerobase.moviereservation.model.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

  UserDetails loadUserByUsername(String email);

  UserDto register(RegisterUserDto.Request request);

  UserDto loginUser(Login.Request request);

  UserDto updateUser(Long userId, UpdateUserDto.Request request);

  void deleteUser(String email);

}
