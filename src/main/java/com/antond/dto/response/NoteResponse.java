package com.antond.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a summary of a note used in list views and filtering operations. This response DTO
 * contains basic note information for displaying in lists, allowing users to browse and select
 * notes before viewing full details.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteResponse {

  private String id;
  private String title;
  private LocalDateTime createdDate;
}
