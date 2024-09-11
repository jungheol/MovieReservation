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
  AUTHORIZATION_ERROR("U_04", "접근 권한이 없습니다."),

  // theater error
  ALREADY_EXISTED_THEATERNAME("T_01", "이미 존재하는 영화관 이름입니다."),
  THEATER_NOT_FOUND("T_02", "해당 영화관을 찾을 수 없습니다."),

  // movie error
  ALREADY_EXISTED_TITLE("M_01", "이미 존재하는 영화 제목입니다."),
  MOVIE_NOT_FOUND("M_02", "해당 영화를 찾을 수 없습니다."),

  // schedule error
  ALREADY_EXISTED_SCHEDULE("S_01", "이미 존재하는 스케쥴입니다."),
  SCHEDULE_NOT_FOUND("S_02", "해당 스케쥴을 찾을 수 없습니다."),

  // reservation error
  ALREADY_EXISTED_RESERVATION("R_01", "이미 존재하는 예약입니다."),
  RESERVATION_NOT_FOUND("R_02", "해당 예약을 찾을 수 없습니다."),
  SEAT_NOT_VALID("R_03", "해당 좌석은 유효하지 않습니다."),
  ALREADY_CANCELED_RESERVATION("R_04", "해당 예약은 이미 취소된 예약입니다."),
  ALREADY_RESERVED_SEAT("R_05", "이미 선점된 좌석입니다."),

  // payment error
  PAYMENT_FAILED("P_01", "결제에 실패했습니다."),

  // review error
  REVIEW_USER_NOT_MATCHED("R_01", "영화 예약자와 리뷰 작성자가 같지 않습니다."),
  REVIEW_ALREADY_EXIST("R_02", "해당 예매에 대한 리뷰가 이미 존재합니다."),
  REVIEW_NOT_AVAILABLE("R_03", "해당 예약건은 리뷰를 쓸 수 있는 상태가 아닙니다."),
  REVIEW_NOT_FOUND("R_04", "해당 리뷰를 찾을 수 없습니다.");

  private final String code;
  private final String description;
}
