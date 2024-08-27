package com.zerobase.moviereservation.model.dto;

import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.model.type.Role;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUser {

  private String email;
  private String password;
  private String username;
  private LocalDateTime birthday;
  private String phoneNumber;

  public static UserDto from(UserDto userDto) {
    return UserDto.builder()
        .email(userDto.getEmail())
        .password(userDto.getPassword())
        .username(userDto.getUsername())
        .birthday(userDto.getBirthday())
        .phoneNumber(userDto.getPhoneNumber())
        .build();
  }
}
