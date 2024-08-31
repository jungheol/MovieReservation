package com.zerobase.moviereservation.model.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UpdateUserDto {

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Request {
    private String password;
    private String username;
    private LocalDate birthday;
    private String phoneNumber;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {
    private String password;
    private String username;
    private LocalDate birthday;
    private String phoneNumber;

    public static Response from(UserDto userDto) {
      return Response.builder()
          .password(userDto.getPassword())
          .username(userDto.getUsername())
          .birthday(userDto.getBirthday())
          .phoneNumber(userDto.getPhoneNumber())
          .build();
    }
  }
}
