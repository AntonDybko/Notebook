package com.antond.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

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

  public ErrorResponse(String message, String details, HttpStatus status, LocalDateTime timestamp) {
    this.message = message;
    this.details = details;
    this.status = status;
    this.statusCode = status.value();
    this.timestamp = timestamp;
  }
}