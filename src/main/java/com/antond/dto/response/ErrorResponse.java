package com.antond.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Represents a standardized error response returned by the API when exceptions occur. This class
 * provides consistent error information across all API endpoints, including error messages, HTTP
 * status details, and timestamps for debugging purposes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

  private String message;
  private String details;
  private HttpStatus status;
  private int statusCode;
  private LocalDateTime timestamp;

  /**
   * Constructs a new ErrorResponse with automatic status code calculation.
   *
   * @param message   a brief, human-readable error message
   * @param details   detailed information about the error
   * @param status    the HTTP status associated with the error
   * @param timestamp the date and time when the error occurred
   */
  public ErrorResponse(String message, String details, HttpStatus status, LocalDateTime timestamp) {
    this.message = message;
    this.details = details;
    this.status = status;
    this.statusCode = status.value();
    this.timestamp = timestamp;
  }
}