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
  ALREADY_EXISTED_EMAIL("U_02", "이미 가입된 이메일입니다.");

  private final String code;
  private final String description;
}
