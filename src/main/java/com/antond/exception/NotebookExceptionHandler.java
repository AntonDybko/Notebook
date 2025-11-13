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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for the Notebook application. This class provides centralized exception
 * handling across all REST controllers, ensuring consistent error responses and appropriate HTTP
 * status codes for different exception types.
 */
@RestControllerAdvice
public class NotebookExceptionHandler {

  /**
   * Handles validation exceptions thrown when request body validation fails. This typically occurs
   * when @Valid annotated objects fail bean validation constraints.
   *
   * @param ex the MethodArgumentNotValidException containing validation error details
   * @return ResponseEntity containing ErrorResponse with validation error details
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
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

  /**
   * Handles malformed JSON in request bodies. This occurs when the request body contains invalid
   * JSON syntax that cannot be parsed.
   *
   * @param ex the HttpMessageNotReadableException containing JSON parsing errors
   * @return ResponseEntity containing ErrorResponse indicating invalid JSON format
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
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

  /**
   * Handles cases when a requested note cannot be found in the system. This typically occurs when
   * trying to access, update, or delete a note with a non-existent ID.
   *
   * @param ex the NoteNotFoundException containing details about the missing note
   * @return ResponseEntity containing ErrorResponse with not found details
   */
  @ResponseStatus(HttpStatus.NOT_FOUND)
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

  /**
   * Handles type mismatch exceptions for method parameters. This occurs when a request parameter
   * cannot be converted to the required type (e.g., passing a string where a number is expected).
   *
   * @param ex the MethodArgumentTypeMismatchException containing type conversion details
   * @return ResponseEntity containing ErrorResponse with type mismatch details
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "Invalid Parameter Type",
        "Parameter '" + ex.getName() + "' should be of type " + ex.getRequiredType()
            .getSimpleName(),
        HttpStatus.BAD_REQUEST,
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles missing required request parameters. This occurs when a required @RequestParam is not
   * provided in the request.
   *
   * @param ex the MissingServletRequestParameterException containing missing parameter details
   * @return ResponseEntity containing ErrorResponse with missing parameter details
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParams(
      MissingServletRequestParameterException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "Missing Parameter",
        "Required parameter '" + ex.getParameterName() + "' is missing",
        HttpStatus.BAD_REQUEST,
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles illegal argument exceptions, typically from service layer validation. This includes
   * cases like invalid pagination parameters (negative page numbers, etc.).
   *
   * @param ex the IllegalArgumentException containing the validation error message
   * @return ResponseEntity containing ErrorResponse with invalid parameter details
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "Invalid Request Parameters",
        ex.getMessage(),
        HttpStatus.BAD_REQUEST,
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Fallback handler for all uncaught exceptions. This ensures that no exception propagates to the
   * client without being handled and provides a generic error response for unexpected server
   * errors.
   *
   * @param ex the Exception that was not handled by more specific exception handlers
   * @return ResponseEntity containing generic ErrorResponse for internal server errors
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
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
