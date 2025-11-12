package com.antond.entity;

import com.antond.constants.Tag;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Represents a note entity stored in MongoDB.
 *
 * <p>This class models a note with title, content, creation timestamps, and organizational tags.
 * It uses Lombok annotations for boilerplate code reduction and is designed for seamless
 * persistence with Spring Data MongoDB.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notes")
public class Note {

  @Id
  private String id;

  @NotBlank(message = "Title is mandatory")
  private String title;

  @NotBlank(message = "Text content is mandatory")
  private String text;

  @Builder.Default
  private LocalDateTime createdDate = LocalDateTime.now();

  private List<Tag> tags;
}
