package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.auth.TokenProvider;
import com.zerobase.moviereservation.model.dto.Login;
import com.zerobase.moviereservation.model.dto.RegisterUserDto;
import com.zerobase.moviereservation.model.dto.UpdateUserDto;
import com.zerobase.moviereservation.model.dto.UserDto;
import com.zerobase.moviereservation.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final TokenProvider tokenProvider;

  @PostMapping("/register/user")
  public RegisterUserDto.Response register(@RequestBody RegisterUserDto.Request request) {
    return RegisterUserDto.Response.from(this.authService.register(request));
  }

  @PostMapping("/login/user")
  public ResponseEntity<?> userLogin(@RequestBody @Valid Login.Request request) {
    UserDto user = this.authService.loginUser(request);
    return ResponseEntity.ok(
        this.tokenProvider.generateToken(
            user.getEmail(),
            user.getRole())
    );
  }

  @PutMapping("/update/user/{userId}")
  public UpdateUserDto.Response updateUser(
      @PathVariable("userId") Long userId,
      @RequestBody @Valid UpdateUserDto.Request request) {
    return UpdateUserDto.Response.from(this.authService.updateUser(userId, request));
  }

  @DeleteMapping("/delete/user")
  public ResponseEntity<?> deleteUser(
      @RequestParam("email") String email
  ) {
    this.authService.deleteUser(email);
    return ResponseEntity.ok("유저 정보가 삭제되었습니다.");
  }
}
