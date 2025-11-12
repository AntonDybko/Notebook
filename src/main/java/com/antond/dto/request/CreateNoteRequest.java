package com.antond.dto.request;

import com.antond.constants.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoteRequest {

  @NotBlank(message = "Title is mandatory")
  private String title;

  @NotBlank(message = "Text content is mandatory")
  private String text;

  private List<Tag> tags;
}
