package com.antond.controller;

import com.antond.constants.Tag;
import com.antond.dto.request.CreateNoteRequest;
import com.antond.dto.request.UpdateNoteRequest;
import com.antond.dto.response.NoteResponse;
import com.antond.entity.Note;
import com.antond.exception.NoteNotFoundException;
import com.antond.mapper.NoteToNoteResponseMapper;
import com.antond.service.NoteService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST controller for managing notes operations. Provides endpoints for creating, retrieving,
 * updating, and deleting notes, as well as filtering notes by tags and retrieving note statistics.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {

  private final NoteService noteService;
  private final NoteToNoteResponseMapper mapper;

  /**
   * Creates a new note with the provided details.
   *
   * @param request the note creation request containing title, content, and tags
   * @return ResponseEntity containing the created note with Location header set to the new resource
   * @throws org.springframework.web.bind.MethodArgumentNotValidException if request validation
   * fails
   */
  @PostMapping
  public ResponseEntity<Note> createNote(@Valid @RequestBody CreateNoteRequest request) {
    Note createdNote = noteService.createNote(request);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(createdNote.getId())
        .toUri();

    return ResponseEntity.created(location).body(createdNote);
  }

  /**
   * Retrieves all notes with pagination support.
   *
   * @param page the page number to retrieve (zero-based, defaults to 0)
   * @param size the number of notes per page (defaults to 10)
   * @return ResponseEntity containing a paginated list of note responses
   * @throws IllegalArgumentException if page is less than 0 or size is less than 1
   */
  @GetMapping
  public ResponseEntity<List<NoteResponse>> getAllNotes(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    List<Note> notes = noteService.getAllNotes(pageable);
    return ResponseEntity.ok(mapper.apply(notes));
  }

  /**
   * Retrieves notes filtered by specified tags with pagination and sorting. Notes are returned in
   * descending order by creation date.
   *
   * @param tags the list of tags to filter notes by (provided in request body)
   * @param page the page number to retrieve (zero-based, defaults to 0)
   * @param size the number of notes per page (defaults to 10)
   * @return ResponseEntity containing a paginated list of note responses matching the specified
   * tags
   * @throws IllegalArgumentException if page is less than 0 or size is less than 1
   */
  @GetMapping("/tag")
  public ResponseEntity<List<NoteResponse>> getNotesByTag(
      @RequestBody List<Tag> tags,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
    List<Note> notes = noteService.getNotesByTag(tags, pageable);
    return ResponseEntity.ok(mapper.apply(notes));
  }

  /**
   * Retrieves a specific note by its unique identifier.
   *
   * @param id the unique identifier of the note to retrieve
   * @return ResponseEntity containing the requested note
   * @throws NoteNotFoundException if no note exists with the given ID
   */
  @GetMapping("/{id}")
  public ResponseEntity<Note> getNoteById(@PathVariable String id) {
    return ResponseEntity.ok(noteService.getNoteById(id));
  }

  /**
   * Retrieves statistics for a specific note. Statistics may include view count, like count, or
   * other relevant metrics.
   *
   * @param id the unique identifier of the note to retrieve statistics for
   * @return ResponseEntity containing a map of statistic names to their values
   * @throws NoteNotFoundException if no note exists with the given ID
   */
  @GetMapping("/{id}/stats")
  public ResponseEntity<Map<String, Long>> getNoteStatsById(@PathVariable String id) {
    return ResponseEntity.ok(noteService.getNoteStatsById(id));
  }

  /**
   * Updates an existing note with new information.
   *
   * @param id      the unique identifier of the note to update
   * @param request the update request containing new title, content, and/or tags
   * @return ResponseEntity containing the updated note
   * @throws NoteNotFoundException                                        if no note exists with the
   *                                                                      given ID
   * @throws org.springframework.web.bind.MethodArgumentNotValidException if request validation
   *                                                                      fails
   */
  @PutMapping("/{id}")
  public ResponseEntity<Note> updateNote(@PathVariable String id,
      @Valid @RequestBody UpdateNoteRequest request) {
    return ResponseEntity.ok(noteService.updateNote(id, request));
  }

  /**
   * Deletes a note by its unique identifier.
   *
   * @param id the unique identifier of the note to delete
   * @return ResponseEntity with no content (HTTP 204) upon successful deletion
   * @throws NoteNotFoundException if no note exists with the given ID
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNote(@PathVariable String id) {
    noteService.deleteNote(id);
    return ResponseEntity.noContent().build();
  }
}
