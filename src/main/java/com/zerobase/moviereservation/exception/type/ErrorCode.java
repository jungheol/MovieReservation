package com.zerobase.moviereservation.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // common error
  INTERNAL_SERVER_ERROR("C_01", "내부 서버 오류가 발생했습니다."),
  INVALID_REQUEST("C_02", "잘못된 요청입니다."),

  // user error
  USER_NOT_FOUND("U_01", "회원을 찾을 수 없습니다."),
  ALREADY_EXISTED_EMAIL("U_02", "이미 가입된 이메일입니다."),
  PASSWORD_NOT_MATCHED("U_03", "비밀번호가 틀렸습니다."),

  // theater error
  ALREADY_EXISTED_THEATERNAME("T_01", "이미 존재하는 영화관 이름입니다."),
  THEATER_NOT_FOUND("T_02", "해당 영화관을 찾을 수 없습니다.");

  private final String code;
  private final String description;
}
