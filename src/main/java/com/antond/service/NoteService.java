package com.antond.service;

import com.antond.constants.Tag;
import com.antond.dto.request.CreateNoteRequest;
import com.antond.dto.request.UpdateNoteRequest;
import com.antond.entity.Note;
import com.antond.exception.NoteNotFoundException;
import com.antond.repository.NoteRepository;
import com.antond.utils.TextUtils;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class containing business logic for note operations. This service acts as an intermediary
 * between controllers and the repository, handling business rules, validation, and coordinating
 * data access operations.
 */
@Service
@RequiredArgsConstructor
public class NoteService {

  private final NoteRepository noteRepository;
  public final TextUtils textUtils;

  /**
   * Creates a new note with the provided request data. The note is built from the request and
   * persisted to the database.
   *
   * @param request the CreateNoteRequest containing title, text, and tags for the new note
   * @return the newly created and persisted Note entity
   */
  public Note createNote(CreateNoteRequest request) {
    System.out.println("about to save note");
    Note note = Note.builder()
        .title(request.getTitle())
        .text(request.getText())
        .tags(request.getTags())
        .build();
    System.out.println("attempt to save note");
    return noteRepository.save(note);
  }

  /**
   * Retrieves a note by its unique identifier.
   *
   * @param id the unique identifier of the note to retrieve
   * @return the found Note entity
   * @throws NoteNotFoundException if no note exists with the given ID
   */
  public Note getNoteById(String id) {
    return noteRepository.findById(id)
        .orElseThrow(() -> new NoteNotFoundException(id));
  }

  /**
   * Calculates and returns word statistics for a specific note. Statistics include word count,
   * character count, and other text metrics.
   *
   * @param id the unique identifier of the note to analyze
   * @return a map of statistic words to their calculated values
   * @throws NoteNotFoundException if no note exists with the given ID
   */
  public Map<String, Long> getNoteStatsById(String id) {
    Note note = noteRepository.findById(id)
        .orElseThrow(() -> new NoteNotFoundException(id));

    return textUtils.calculateWordStats(note.getText());
  }

  /**
   * Retrieves all notes with pagination support, ordered by creation date descending.
   *
   * @param pageable the pagination information including page number and size
   * @return a paginated list of notes sorted by creation date (newest first)
   */
  public List<Note> getAllNotes(Pageable pageable) {
    return noteRepository.findAllByOrderByCreatedDateDesc(pageable);
  }

  /**
   * Retrieves notes filtered by specified tags with pagination. If the tags list is empty, returns
   * all notes (same as getAllNotes).
   *
   * @param tags     the list of tags to filter by; notes must contain ALL specified tags
   * @param pageable the pagination information including page number, size and sorting order
   * @return a paginated list of notes matching the tag filter, or all notes if tags is empty
   */
  public List<Note> getNotesByTag(List<Tag> tags, Pageable pageable) {
    if (!tags.isEmpty()) {
      return noteRepository.findByTagsContainingAll(tags, pageable);
    } else {
      return this.getAllNotes(pageable);
    }
  }

  /**
   * Updates an existing note with partial data from the update request. Only non-null fields in the
   * request will be updated; null fields preserve existing values.
   *
   * @param id      the unique identifier of the note to update
   * @param request the UpdateNoteRequest containing the fields to update
   * @return the updated Note entity
   * @throws NoteNotFoundException if no note exists with the given ID
   */
  public Note updateNote(String id, UpdateNoteRequest request) {
    return noteRepository.findById(id)
        .map(existingNote -> {
          if (request.getTitle() != null) {
            existingNote.setTitle(request.getTitle());
          }
          if (request.getText() != null) {
            existingNote.setText(request.getText());
          }
          if (request.getTags() != null) {
            existingNote.setTags(request.getTags());
          }
          return noteRepository.save(existingNote);
        })
        .orElseThrow(() -> new NoteNotFoundException(id));
  }

  /**
   * Deletes a note by its unique identifier. Verifies the note exists before attempting deletion to
   * ensure proper error handling.
   *
   * @param id the unique identifier of the note to delete
   * @throws NoteNotFoundException if no note exists with the given ID
   */
  public void deleteNote(String id) {
    noteRepository.findById(id)
        .orElseThrow(() -> new NoteNotFoundException(id));
    noteRepository.deleteById(id);
  }
}