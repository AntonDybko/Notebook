package com.antond.exception;

import com.antond.dto.response.ErrorResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class NotebookExceptionHandler {
  // Handle validation errors (Bean Validation)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ErrorResponse errorResponse = new ErrorResponse(
        "Validation Failed",
        errors.toString(),
        HttpStatus.BAD_REQUEST,
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // Handle invalid JSON format
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleInvalidJsonFormat(HttpMessageNotReadableException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "Invalid JSON Format",
        "The request body contains invalid JSON",
        HttpStatus.BAD_REQUEST,
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // Handle note not found
  @ExceptionHandler(NoteNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoteNotFoundException(NoteNotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "Note Not Found",
        ex.getMessage(),
        HttpStatus.NOT_FOUND,
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  // Handle type mismatch (e.g., string instead of number for page/size)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "Invalid Parameter Type",
        "Parameter '" + ex.getName() + "' should be of type " + ex.getRequiredType().getSimpleName(),
        HttpStatus.BAD_REQUEST,
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // Handle missing request parameters
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "Missing Parameter",
        "Required parameter '" + ex.getParameterName() + "' is missing",
        HttpStatus.BAD_REQUEST,
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // Handle all other exceptions
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "Internal Server Error",
        "An unexpected error occurred",
        HttpStatus.INTERNAL_SERVER_ERROR,
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
