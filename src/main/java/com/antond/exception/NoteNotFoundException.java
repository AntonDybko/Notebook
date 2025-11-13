package com.antond.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested note cannot be found in the system. This exception is typically
 * thrown by service methods when operations are attempted on notes that don't exist or have been
 * deleted.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoteNotFoundException extends RuntimeException {

  /**
   * Constructs a new NoteNotFoundException with a descriptive message. The message includes the
   * specific note ID that could not be found, helping clients identify which resource was missing.
   *
   * @param id the unique identifier of the note that was not found
   */
  public NoteNotFoundException(String id) {
    super(String.format("Note with id %s not found", id));
  }
}
