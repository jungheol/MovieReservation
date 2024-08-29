package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.auth.TokenProvider;
import com.zerobase.moviereservation.model.dto.Login;
import com.zerobase.moviereservation.model.dto.RegisterUser;
import com.zerobase.moviereservation.model.dto.UserDto;
import com.zerobase.moviereservation.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final TokenProvider tokenProvider;

  @PostMapping("/register/user")
  public RegisterUser.Response register(@RequestBody RegisterUser.Request request) {
    return RegisterUser.Response.from(this.authService.register(request));
  }

  @PostMapping("/login/user")
  public ResponseEntity<?> userLogin(@RequestBody @Valid Login.Request request) {
    UserDto user = this.authService.login(request);
    return ResponseEntity.ok(
        this.tokenProvider.generateToken(
            user.getEmail(),
            user.getRole())
    );
  }
}
