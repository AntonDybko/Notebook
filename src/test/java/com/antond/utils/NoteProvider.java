package com.antond.utils;

import com.antond.constants.Tag;
import com.antond.entity.Note;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class NoteProvider {

  @Autowired
  protected MongoTemplate mongoTemplate;

  public String createTestNote() {
    Note note = Note.builder()
        .title("Test Note")
        .text("Test content")
        .tags(List.of(Tag.PERSONAL))
        .createdDate(LocalDateTime.now())
        .build();

    Note savedNote = mongoTemplate.insert(note);
    return savedNote.getId();
  }

  public String createNoteWithText(String text) {
    Note note = Note.builder()
        .title("Stats Test")
        .text(text)
        .createdDate(LocalDateTime.now())
        .build();

    Note savedNote = mongoTemplate.insert(note);
    return savedNote.getId();
  }

  public String createNoteWithTags(String title, List<Tag> tags) {
    Note note = Note.builder()
        .title(title)
        .text("Content")
        .tags(tags)
        .createdDate(LocalDateTime.now())
        .build();

    Note savedNote = mongoTemplate.insert(note);
    return savedNote.getId();
  }

  public void createTestNotes(int count) {
    List<Note> notes = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Note note = Note.builder()
          .title("Note " + i)
          .text("Content " + i)
          .createdDate(LocalDateTime.now())
          .build();
      notes.add(note);
    }
    mongoTemplate.insertAll(notes);
    System.out.println("✅ Created " + count + " test notes via MongoTemplate");
  }


  /**
   * Creates a standard set of test notes that can be used for both getAllNotes and getNotesByTag
   * tests
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
