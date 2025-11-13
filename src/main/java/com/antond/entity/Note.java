package com.antond.entity;

import com.antond.constants.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Represents a note entity stored in the database. This is the main domain object that contains all
 * note data including content, metadata, and  tags.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notes")
public class Note {

  @Id
  private String id;

  private String title;

  private String text;

  @Builder.Default
  private LocalDateTime createdDate = LocalDateTime.now();

  private List<Tag> tags;
}
