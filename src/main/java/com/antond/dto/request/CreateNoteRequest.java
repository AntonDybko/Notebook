package com.antond.dto.request;

import com.antond.constants.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Represents a request to create a new note. This class encapsulates all the required information
 * needed to create a note entity. Validation annotations ensure that mandatory fields are provided
 * and meet basic requirements.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoteRequest {

  @NotBlank(message = "Title is mandatory")
  private String title;

  @NotBlank(message = "Text content is mandatory")
  private String text;

  @NonNull
  @Builder.Default
  private List<Tag> tags = new ArrayList<>();
}
