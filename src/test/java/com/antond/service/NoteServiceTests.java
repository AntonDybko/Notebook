package com.antond.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.antond.constants.Tag;
import com.antond.dto.request.CreateNoteRequest;
import com.antond.dto.request.UpdateNoteRequest;
import com.antond.entity.Note;
import com.antond.exception.NoteNotFoundException;
import com.antond.repository.NoteRepository;
import com.antond.utils.TextUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NoteServiceTests {

  @Mock
  private NoteRepository noteRepository;

  @Mock
  private TextUtils textUtils;

  @Mock
  private Pageable pageable;

  @InjectMocks
  private NoteService noteService;

  private final String NOTE_ID = "507f1f77bcf86cd799439011";
  private final Note sampleNote = Note.builder()
      .id(NOTE_ID)
      .title("Test Title")
      .text("Test content for the note")
      .createdDate(LocalDateTime.now())
      .tags(List.of(Tag.PERSONAL))
      .build();

  @Test
  void createNote_ValidRequest_ReturnsSavedNote() {
    CreateNoteRequest request = new CreateNoteRequest(
        "Test Title",
        "Test content",
        List.of(Tag.PERSONAL)
    );

    when(noteRepository.save(any(Note.class))).thenReturn(sampleNote);

    Note result = noteService.createNote(request);

    assertNotNull(result);
    assertEquals(NOTE_ID, result.getId());
    assertEquals("Test Title", result.getTitle());
    assertEquals("Test content for the note", result.getText());
    verify(noteRepository, times(1)).save(any(Note.class));
  }

  @Test
  void createNote_WithEmptyTags_ReturnsNoteWithEmptyTags() {
    CreateNoteRequest request = new CreateNoteRequest(
        "Test Title",
        "Test content",
        List.of()
    );

    Note noteWithEmptyTags = Note.builder()
        .id(NOTE_ID)
        .title("Test Title")
        .text("Test content for the note")
        .tags(List.of())
        .build();

    when(noteRepository.save(any(Note.class))).thenReturn(noteWithEmptyTags);

    Note result = noteService.createNote(request);

    assertNotNull(result);
    assertTrue(result.getTags().isEmpty());
    verify(noteRepository, times(1)).save(any(Note.class));
  }

  @Test
  void getNoteById_ExistingId_ReturnsNote() {
    when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(sampleNote));

    Note result = noteService.getNoteById(NOTE_ID);

    assertNotNull(result);
    assertEquals(NOTE_ID, result.getId());
    verify(noteRepository, times(1)).findById(NOTE_ID);
  }

  @Test
  void getNoteById_NonExistingId_ThrowsNoteNotFoundException() {
    when(noteRepository.findById(anyString())).thenReturn(Optional.empty());

    assertThrows(NoteNotFoundException.class, () -> {
      noteService.getNoteById("non-existing-id");
    });

    verify(noteRepository, times(1)).findById("non-existing-id");
  }

  @Test
  void getNoteStatsById_ExistingNote_ReturnsWordStats() {
    when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(sampleNote));

    Map<String, Long> expectedStats = Map.of("test", 2L, "content", 1L);
    when(textUtils.calculateWordStats(sampleNote.getText())).thenReturn(expectedStats);

    Map<String, Long> result = noteService.getNoteStatsById(NOTE_ID);

    assertNotNull(result);
    assertEquals(expectedStats, result);
    verify(noteRepository, times(1)).findById(NOTE_ID);
    verify(textUtils, times(1)).calculateWordStats(sampleNote.getText());
  }

  @Test
  void getNoteStatsById_NonExistingNote_ThrowsNoteNotFoundException() {
    when(noteRepository.findById(anyString())).thenReturn(Optional.empty());

    assertThrows(NoteNotFoundException.class, () -> {
      noteService.getNoteStatsById("non-existing-id");
    });

    verify(noteRepository, times(1)).findById("non-existing-id");
    verify(textUtils, never()).calculateWordStats(anyString());
  }

  @Test
  void getAllNotes_WithPageable_ReturnsNotesList() {
    List<Note> expectedNotes = List.of(sampleNote);
    when(noteRepository.findAllByOrderByCreatedDateDesc(pageable)).thenReturn(expectedNotes);

    List<Note> result = noteService.getAllNotes(pageable);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(sampleNote, result.get(0));
    verify(noteRepository, times(1)).findAllByOrderByCreatedDateDesc(pageable);
  }

  @Test
  void getNotesByTag_WithTags_ReturnsFilteredNotes() {
    List<Tag> tags = List.of(Tag.PERSONAL);
    List<Note> expectedNotes = List.of(sampleNote);

    when(noteRepository.findByTagsContainingAll(tags, pageable))
        .thenReturn(expectedNotes);

    List<Note> result = noteService.getNotesByTag(tags, pageable);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(noteRepository, times(1)).findByTagsContainingAll(tags, pageable);
    verify(noteRepository, never()).findAllByOrderByCreatedDateDesc(any(Pageable.class));
  }

  @Test
  void getNotesByTag_WithEmptyTags_ReturnsAllNotes() {
    List<Tag> emptyTags = List.of();
    List<Note> expectedNotes = List.of(sampleNote);

    when(noteRepository.findAllByOrderByCreatedDateDesc(pageable)).thenReturn(expectedNotes);

    List<Note> result = noteService.getNotesByTag(emptyTags, pageable);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(noteRepository, times(1)).findAllByOrderByCreatedDateDesc(pageable);
    verify(noteRepository, never()).findByTagsContainingAll(any(), any());
  }

  @Test
  void updateNote_ExistingNote_ReturnsUpdatedNote() {
    UpdateNoteRequest updateRequest = new UpdateNoteRequest(
        "Updated Title",
        "Updated content",
        List.of(Tag.BUSINESS)
    );

    Note updatedNote = Note.builder()
        .id(NOTE_ID)
        .title("Updated Title")
        .text("Updated content")
        .tags(List.of(Tag.BUSINESS))
        .build();

    when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(sampleNote));
    when(noteRepository.save(any(Note.class))).thenReturn(updatedNote);

    Note result = noteService.updateNote(NOTE_ID, updateRequest);

    assertNotNull(result);
    assertEquals("Updated Title", result.getTitle());
    assertEquals("Updated content", result.getText());
    assertEquals(List.of(Tag.BUSINESS), result.getTags());
    verify(noteRepository, times(1)).findById(NOTE_ID);
    verify(noteRepository, times(1)).save(sampleNote);
  }

  @Test
  void updateNote_PartialUpdate_ReturnsPartiallyUpdatedNote() {
    UpdateNoteRequest partialRequest = new UpdateNoteRequest(
        "Updated Title",
        null,
        null
    );

    Note updatedNote = Note.builder()
        .id(NOTE_ID)
        .title("Updated Title")
        .text(sampleNote.getText())
        .tags(sampleNote.getTags())
        .build();

    when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(sampleNote));
    when(noteRepository.save(any(Note.class))).thenReturn(updatedNote);

    Note result = noteService.updateNote(NOTE_ID, partialRequest);

    assertNotNull(result);
    assertEquals("Updated Title", result.getTitle());
    assertEquals(sampleNote.getText(), result.getText());
    assertEquals(sampleNote.getTags(), result.getTags());
    verify(noteRepository, times(1)).findById(NOTE_ID);
    verify(noteRepository, times(1)).save(sampleNote);
  }

  @Test
  void updateNote_NonExistingNote_ThrowsNoteNotFoundException() {
    UpdateNoteRequest updateRequest = new UpdateNoteRequest("Title", "Content", List.of());
    when(noteRepository.findById(anyString())).thenReturn(Optional.empty());

    assertThrows(NoteNotFoundException.class, () -> {
      noteService.updateNote("non-existing-id", updateRequest);
    });

    verify(noteRepository, times(1)).findById("non-existing-id");
    verify(noteRepository, never()).save(any(Note.class));
  }

  @Test
  void deleteNote_ExistingNote_DeletesSuccessfully() {
    when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(sampleNote));
    doNothing().when(noteRepository).deleteById(NOTE_ID);

    noteService.deleteNote(NOTE_ID);

    verify(noteRepository, times(1)).findById(NOTE_ID);
    verify(noteRepository, times(1)).deleteById(NOTE_ID);
  }

  @Test
  void deleteNote_NonExistingNote_ThrowsNoteNotFoundException() {
    when(noteRepository.findById(anyString())).thenReturn(Optional.empty());

    assertThrows(NoteNotFoundException.class, () -> {
      noteService.deleteNote("non-existing-id");
    });

    verify(noteRepository, times(1)).findById("non-existing-id");
    verify(noteRepository, never()).deleteById(anyString());
  }
}
