package com.zerobase.moviereservation.model.dto;

import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.model.type.Role;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class RegisterUser {

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Request {
    private String email;
    private String password;
    private String username;
    private LocalDateTime birthday;
    private String phoneNumber;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {
    private String email;
    private String username;

    public static Response from(UserDto userDto) {
      return Response.builder()
          .email(userDto.getEmail())
          .username(userDto.getUsername())
          .build();
    }
  }
}
