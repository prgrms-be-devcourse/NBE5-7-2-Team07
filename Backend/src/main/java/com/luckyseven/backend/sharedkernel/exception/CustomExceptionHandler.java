package com.luckyseven.backend.sharedkernel.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

  /*
   * Custom Exception
   * */
  @ExceptionHandler(CustomLogicException.class)
  protected ResponseEntity<ErrorResponse> handleCustomException(final CustomLogicException e) {
    log.error("handleCustomException: {}");
    return ResponseEntity
        .status(e.getExceptionCode().getHttpStatus())
        .body(ErrorResponse.of(e));
  }

}
