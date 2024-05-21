package bio.terra.appmanager.controller;

import bio.terra.appmanager.api.model.ErrorReport;
import bio.terra.common.exception.AbstractGlobalExceptionHandler;
import bio.terra.common.exception.InconsistentFieldsException;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends AbstractGlobalExceptionHandler<ErrorReport> {

  @Override
  public ErrorReport generateErrorReport(Throwable ex, HttpStatus statusCode, List<String> causes) {
    System.out.println("in error report " + statusCode.toString());
    return new ErrorReport().message(ex.getMessage()).statusCode(statusCode.value());
  }

  // TODO: test this on running app?
  @ExceptionHandler(InconsistentFieldsException.class)
  public ResponseEntity<ErrorReport> constraintViolationExceptionHandler(
      ConstraintViolationException ex) {
    System.out.println("in handler");
    ErrorReport errorReport =
        new ErrorReport().message(ex.getMessage()).statusCode(HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(errorReport, HttpStatus.BAD_REQUEST);
  }
}
