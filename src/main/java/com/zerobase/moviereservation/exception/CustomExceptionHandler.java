package com.zerobase.moviereservation.exception;

import static com.zerobase.moviereservation.exception.type.ErrorCode.INTERNAL_SERVER_ERROR;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ErrorResponse handleCommonException(CustomException e) {
    log.error("{} is occurred.", e.getErrorCode());

    return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
  }

  @ExceptionHandler(Exception.class)
  public ErrorResponse handleException(Exception e) {
    log.error("Exception is occurred.", e);

    return new ErrorResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getDescription());
  }
}
