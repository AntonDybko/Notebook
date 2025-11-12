package com.antond.service;

import com.antond.constants.Tag;
import com.antond.dto.request.CreateNoteRequest;
import com.antond.dto.request.UpdateNoteRequest;
import com.antond.entity.Note;
import com.antond.exception.NoteNotFoundException;
import com.antond.repository.NoteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {

  private final NoteRepository noteRepository;

  public Note createNote(CreateNoteRequest request) {
    Note note = Note.builder()
        .title(request.getTitle())
        .text(request.getText())
        .tags(request.getTags())
        .build();
    return noteRepository.save(note);
  }

  // Read - Single note by ID
  public Note getNoteById(String id) {
    return noteRepository.findById(id)
        .orElseThrow(() -> new NoteNotFoundException(id));
  }

  // Read - All notes
  public List<Note> getAllNotes(Pageable pageable) {
    return noteRepository.findAllByOrderByCreatedDateDesc(pageable);
  }

  public List<Note> getNotesByTag(List<Tag> tags, Pageable pageable) {
    return noteRepository.findByTagsInOrderByCreatedDateDesc(tags, pageable);
  }

  // Update
  public Note updateNote(String id, UpdateNoteRequest request) {
    return noteRepository.findById(id)
        .map(existingNote -> {
          if (request.getTitle() != null) {
            existingNote.setTitle(request.getTitle());
          }
          if (request.getText() != null) {
            existingNote.setText(request.getText());
          }
          if (request.isTagsProvided()) {
            existingNote.setTags(request.getTags());
          }
          return noteRepository.save(existingNote);
        })
        .orElseThrow(() -> new NoteNotFoundException(id));
  }

  // Delete
  public void deleteNote(String id) {
    noteRepository.findById(id)
        .orElseThrow(() -> new NoteNotFoundException(id));
    noteRepository.deleteById(id);
  }
}
