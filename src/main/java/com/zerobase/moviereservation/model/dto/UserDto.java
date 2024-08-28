package com.zerobase.moviereservation.model.dto;

import com.zerobase.moviereservation.entity.User;
import com.zerobase.moviereservation.model.type.Role;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  private Long id;
  private String email;
  private String password;
  private String username;
  private LocalDate birthday;
  private String phoneNumber;
  private Role role;

  public static UserDto fromEntity(User user) {
    return UserDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .password(user.getPassword())
        .username(user.getUsername())
        .birthday(user.getBirthday())
        .phoneNumber(user.getPhoneNumber())
        .role(user.getRole())
        .build();
  }
}
