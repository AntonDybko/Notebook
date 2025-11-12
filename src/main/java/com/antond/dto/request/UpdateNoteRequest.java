package com.antond.dto.request;

import com.antond.constants.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoteRequest {

  private String title;

  private String text;

  private List<Tag> tags;

  private boolean tagsProvided = false;
}
