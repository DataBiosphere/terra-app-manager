package bio.terra.appmanager.controller;

import bio.terra.appmanager.api.model.ErrorReport;
import bio.terra.common.exception.AbstractGlobalExceptionHandler;
import bio.terra.common.exception.InconsistentFieldsException;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// https://docs.sentry.io/platforms/java/guides/spring-boot/#configure
// ensures this is run before Sentry's ExceptionHandler
@Order(Integer.MIN_VALUE)
public class GlobalExceptionHandler extends AbstractGlobalExceptionHandler<ErrorReport> {

  @Override
  public ErrorReport generateErrorReport(Throwable ex, HttpStatus statusCode, List<String> causes) {
    return new ErrorReport().message(ex.getMessage()).statusCode(statusCode.value());
  }

  @ExceptionHandler(InconsistentFieldsException.class)
  public ResponseEntity<ErrorReport> constraintViolationExceptionHandler(
      InconsistentFieldsException ex) {
    ErrorReport errorReport =
        new ErrorReport().message(ex.getMessage()).statusCode(HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(errorReport, HttpStatus.BAD_REQUEST);
  }
}
