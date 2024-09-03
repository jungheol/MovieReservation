package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.model.dto.Login;
import com.zerobase.moviereservation.model.dto.RegisterUserDto;
import com.zerobase.moviereservation.model.dto.UpdateUserDto;
import com.zerobase.moviereservation.model.dto.UserDto;
import com.zerobase.moviereservation.model.type.Role;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

  UserDetails loadUserByUsername(String email);

  UserDto registerUser(RegisterUserDto.Request request, Role role);

  UserDto loginUser(Login.Request request);

  UserDto updateUser(Long userId, UpdateUserDto.Request request);

  void deleteUser(String email);

}
