package com.antond.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoteNotFoundException extends RuntimeException {

  public NoteNotFoundException(String id) {
    super(String.format("Note with id %s not found", id));
  }
}
