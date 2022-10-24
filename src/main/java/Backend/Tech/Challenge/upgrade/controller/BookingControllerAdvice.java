package Backend.Tech.Challenge.upgrade.controller;

import Backend.Tech.Challenge.upgrade.exception.BadRequestException;
import Backend.Tech.Challenge.upgrade.exception.BookingNotFoundException;
import Backend.Tech.Challenge.upgrade.exception.ConflictException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import org.springframework.beans.BeansException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@ControllerAdvice
public class BookingControllerAdvice {

  private ErrorResponse errorHandler(HttpStatus status, Throwable throwable) {
    return ErrorResponse.builder()
        .status(status)
        .message(throwable.getMessage())
        .build();
  }

  @ResponseBody
  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ErrorResponse badRequestHandler(BadRequestException e) {
    return errorHandler(HttpStatus.BAD_REQUEST, e);
  }

  @ResponseBody
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ErrorResponse dateTimeParsingHandler(HttpMessageNotReadableException e) {
    return errorHandler(HttpStatus.BAD_REQUEST, e);
  }

  @ResponseBody
  @ExceptionHandler(BeansException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ErrorResponse beansExceptionHandler(BeansException e) {
    return errorHandler(HttpStatus.BAD_REQUEST, e);
  }

  @ResponseBody
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ErrorResponse constraintViolationHandler(BindException e) {
    final var message = e.getBindingResult().getAllErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(". "))
        .concat(".");
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST)
        .message(message)
        .build();
  }

  @ResponseBody
  @ExceptionHandler(BookingNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  ErrorResponse notFoundHandler(BookingNotFoundException e) {
    return errorHandler(HttpStatus.NOT_FOUND, e);
  }

  @ResponseBody
  @ExceptionHandler(ConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  ErrorResponse badRequestHandler(ConflictException e) {
    return errorHandler(HttpStatus.CONFLICT, e);
  }

  @ResponseBody
  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  ErrorResponse internalServerErrorHandler(RuntimeException e) {
    return ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .message(e.getMessage())
        .build();
  }

  @Value
  @Builder
  @Schema(title = "Error response", description = "Detailed information about an error")
  private static class ErrorResponse {
    @Schema(description = "HTTP status")
    HttpStatus status;
    @Schema(description = "Description of the error")
    String message;
  }
}
