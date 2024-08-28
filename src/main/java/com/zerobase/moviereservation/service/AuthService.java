package com.zerobase.moviereservation.service;

import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.model.dto.Login;
import com.zerobase.moviereservation.model.dto.RegisterUser;
import com.zerobase.moviereservation.model.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

  UserDetails loadUserByUsername(String email);

  UserDto register(RegisterUser.Request request);
}
