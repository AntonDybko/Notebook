package com.antond.mapper;

import com.antond.dto.response.NoteResponse;
import com.antond.entity.Note;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class NoteToNoteResponseMapper implements Function<List<Note>,
    List<NoteResponse>> {

  @Override
  public List<NoteResponse> apply(List<Note> notes) {
    return notes.stream().map(n -> NoteResponse.builder()
        .id(n.getId())
        .title(n.getTitle())
        .createdDate(n.getCreatedDate())
        .build()).toList();
  }
}
