package com.antond.dto.request;

import com.antond.constants.Tag;
import com.antond.validation.NotBlankIfPresent;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request to update an existing note. This class allows partial updates where any
 * field can be optionally provided. Fields that are not provided (null) will not be updated, while
 * provided fields must meet validation requirements.
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UpdateNoteRequest {

  @NotBlankIfPresent(message = "Title cannot be empty if provided")
  private String title;

  @NotBlankIfPresent(message = "Text content cannot be empty if provided")
  private String text;

  private List<Tag> tags;
}
