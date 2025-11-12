package com.antond.controller;

import com.antond.constants.Tag;
import com.antond.dto.request.CreateNoteRequest;
import com.antond.dto.request.UpdateNoteRequest;
import com.antond.dto.response.NoteResponse;
import com.antond.entity.Note;
import com.antond.mapper.NoteToNoteResponseMapper;
import com.antond.service.NoteService;
import com.antond.utils.TextUtils;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {

  private final NoteService noteService;
  private final NoteToNoteResponseMapper mapper;

  //wrong format
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

  //page or size less than 0
  @GetMapping
  public ResponseEntity<List<NoteResponse>> getAllNotes(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    List<Note> notes = noteService.getAllNotes(pageable);
    return ResponseEntity.ok(mapper.apply(notes));
  }

  //page or size less than 0
  @GetMapping("/tag")
  public ResponseEntity<List<NoteResponse>> getNotesByTag(
      @RequestBody List<Tag> tags,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    List<Note> notes = noteService.getNotesByTag(tags, pageable);
    return ResponseEntity.ok(mapper.apply(notes));
  }

  //not found
  @GetMapping("/{id}")
  public ResponseEntity<Note> getNoteById(@PathVariable String id) {
    Note note = noteService.getNoteById(id);
    return ResponseEntity.ok(note);
  }

  //not found
  @GetMapping("/{id}/stats")
  public ResponseEntity<Map<String, Long>> getNoteStatsById(@PathVariable String id) {
    Note note = noteService.getNoteById(id);

    return ResponseEntity.ok(TextUtils.calculateWordStats(note.getText()));
  }

  //not found
  //wrong format
  @PutMapping("/{id}")
  public ResponseEntity<Note> updateNote(@PathVariable String id, @Valid @RequestBody UpdateNoteRequest request) {
    Note updatedNote = noteService.updateNote(id, request);
    return ResponseEntity.ok(updatedNote);
  }

  //not found
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNote(@PathVariable String id) {
    noteService.deleteNote(id);
    return ResponseEntity.noContent().build();
  }
}
