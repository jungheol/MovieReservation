package com.zerobase.moviereservation.exception;

import com.zerobase.moviereservation.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  private ErrorCode errorcode;
  private String errorMessage;
}
