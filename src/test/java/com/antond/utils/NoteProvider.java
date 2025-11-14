package com.antond.utils;

import com.antond.constants.Tag;
import com.antond.entity.Note;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * Service class for creating and managing test notes in the database. This provider is primarily
 * used for testing purposes to populate the database with sample data for integration tests,
 * development, and demonstration.
 */
@Service
public class NoteProvider {

  @Autowired
  protected MongoTemplate mongoTemplate;

  /**
   * Creates a new note with the specified parameters, using default values for any null parameters.
   * This method provides flexibility for creating notes with partial data while ensuring all
   * required fields have sensible defaults.
   *
   * @param title       the title of the note; if null, defaults to "Test Note"
   * @param text        the text content of the note; if null, defaults to "Content"
   * @param tags        the list of tags associated with the note; if null, defaults to empty list
   * @param createdDate the creation date of the note; if null, defaults to current date/time
   * @return the unique identifier of the newly created note
   */
  public String createNote(String title, String text, List<Tag> tags, LocalDateTime createdDate) {
    Note note = Note.builder()
        .title(title != null ? title : "Test Note")
        .text(text != null ? text : "Content")
        .tags(tags != null ? tags : List.of())
        .createdDate(createdDate != null ? createdDate : LocalDateTime.now())
        .build();

    Note savedNote = mongoTemplate.insert(note);
    return savedNote.getId();
  }

  /**
   * Creates a basic test note with personal tag. This method is useful for quick testing when only
   * a simple note is needed.
   *
   * @return the unique identifier of the created test note
   */
  public String createTestNote() {
    return createNote("Test Note", "Test content", List.of(Tag.PERSONAL), null);
  }

  /**
   * Creates a note with custom text content, using default title and no specific tags. This method
   * is particularly useful for testing text analysis and statistics features.
   *
   * @param text the custom text content to use for the note
   * @return the unique identifier of the created note
   */
  public String createNoteWithText(String text) {
    return createNote("Stats Test", text, null, null);
  }

  /**
   * Creates multiple test notes in batch for performance testing or bulk operations. Each note has
   * a sequentially numbered title and content for easy identification.
   *
   * @param count the number of test notes to create
   */
  public void createTestNotes(int count) {
    List<Note> notes = IntStream.range(0, count)
        .mapToObj(i -> Note.builder()
            .title("Note " + i)
            .text("Content " + i)
            .tags(List.of())
            .createdDate(LocalDateTime.now())
            .build())
        .toList();

    mongoTemplate.insertAll(notes);
  }

  /**
   * Creates a standardized set of test notes specifically designed for pagination and tag filtering
   * tests. The created notes include: - Personal notes with varying creation dates (oldest to
   * newest) - Business notes with varying creation dates (oldest to newest) - Mixed tags (some
   * notes have multiple tags) - Timestamps that ensure predictable sorting by creation date This
   * method is ideal for testing scenarios that require predictable data for verifying pagination,
   * sorting, and tag-based filtering functionality.
   */
  public void createNotesForPaginationTesting() {

    LocalDateTime now = LocalDateTime.now();
    List<Note> notes = List.of(
        Note.builder().title("Personal Note 1 - Oldest").text("Content").tags(List.of(Tag.PERSONAL))
            .createdDate(now.minusHours(5)).build(),
        Note.builder().title("Personal Note 2").text("Content").tags(List.of(Tag.PERSONAL))
            .createdDate(now.minusHours(3)).build(),
        Note.builder().title("Personal Note 3 - Newest").text("Content")
            .tags(List.of(Tag.PERSONAL, Tag.IMPORTANT)).createdDate(now.minusMinutes(30)).build(),
        Note.builder().title("Business Note 1 - Oldest").text("Content").tags(List.of(Tag.BUSINESS))
            .createdDate(now.minusHours(4)).build(),
        Note.builder().title("Business Note 2").text("Content").tags(List.of(Tag.BUSINESS))
            .createdDate(now.minusHours(2)).build(),
        Note.builder().title("Business Note 3 - Newest").text("Content")
            .tags(List.of(Tag.BUSINESS, Tag.IMPORTANT)).createdDate(now.minusMinutes(15)).build()
    );

    mongoTemplate.insertAll(notes);
    System.out.println("✅ Created " + notes.size() + " test notes");
  }
}