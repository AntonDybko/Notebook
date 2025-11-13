package com.antond.mapper;

import com.antond.dto.response.NoteResponse;
import com.antond.entity.Note;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * Mapper component that converts a list of Note entities to a list of NoteResponse DTOs. This
 * mapper is used to transform database entities into API response objects, ensuring that only the
 * necessary fields are exposed in list views and filtering operations.
 */
@Component
public class NoteToNoteResponseMapper implements Function<List<Note>,
    List<NoteResponse>> {

  /**
   * Transforms a list of Note entities into a list of NoteResponse DTOs. This method extracts only
   * the essential fields (id, title, createdDate) needed for displaying notes in list views,
   * leaving out detailed content and other fields.
   *
   * @param notes the list of Note entities to convert
   * @return a list of NoteResponse DTOs containing simplified note information
   */
  @Override
  public List<NoteResponse> apply(List<Note> notes) {
    return notes.stream().map(n -> NoteResponse.builder()
        .id(n.getId())
        .title(n.getTitle())
        .createdDate(n.getCreatedDate())
        .build()).toList();
  }
}
