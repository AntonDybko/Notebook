package com.antond.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.antond.config.IntegrationTest;
import com.antond.constants.Tag;
import com.antond.dto.request.CreateNoteRequest;
import com.antond.dto.request.UpdateNoteRequest;
import com.antond.utils.NoteProvider;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

class NoteControllerTests extends IntegrationTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private NoteProvider noteProvider;

  @Test
  void testConnection() {
    assertDoesNotThrow(() -> {
      String dbName = mongoTemplate.getDb().getName();
    });
  }

  @Test
  void createNote_ValidRequest_ReturnsCreated() {
    CreateNoteRequest request = CreateNoteRequest.builder()
        .title("Test Note")
        .text("This is test content")
        .tags(List.of(Tag.PERSONAL, Tag.IMPORTANT))
        .build();

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/notes")
        .then()
        .statusCode(201)
        .header("Location", notNullValue())
        .body("title", equalTo("Test Note"))
        .body("text", equalTo("This is test content"))
        .body("tags", hasItems("PERSONAL", "IMPORTANT"));
  }

  @Test
  void createNote_InvalidRequest_ReturnsBadRequest() {
    CreateNoteRequest invalidRequest = CreateNoteRequest.builder()
        .title("")
        .text("")
        .build();

    given()
        .contentType(ContentType.JSON)
        .body(invalidRequest)
        .when()
        .post("/notes")
        .then()
        .statusCode(400)
        .body("message", equalTo("Validation Failed"));
  }

  @Test
  void getAllNotes_ValidPagination_ShouldReturnCorrectPage() {
    noteProvider.createNotesForPaginationTesting();
    given()
        .when()
        .get("/notes?page=0&size=2")
        .then()
        .statusCode(200)
        .body("size()", equalTo(2))
        .body("[0].title", equalTo("Business Note 3 - Newest"))
        .body("[1].title", equalTo("Personal Note 3 - Newest"));
  }

  @Test
  void getAllNotes_WithSecondPage_ShouldReturnNextNotes() {
    noteProvider.createNotesForPaginationTesting();
    given()
        .when()
        .get("/notes?page=1&size=2")
        .then()
        .statusCode(200)
        .body("size()", equalTo(2))
        .body("[0].title", equalTo("Business Note 2"))
        .body("[1].title", equalTo("Personal Note 2"));
  }

  @Test
  void getAllNotes_InvalidPage_ReturnsBadRequest() {
    given()
        .when()
        .get("/notes?page=-1&size=10")
        .then()
        .statusCode(400);
  }

  @Test
  void getAllNotes_InvalidSize_ReturnsBadRequest() {
    given()
        .when()
        .get("/notes?page=0&size=0")
        .then()
        .statusCode(400);
  }

  @Test
  void getNotesByTag_WithPagination_ShouldReturnFilteredPage() {
    noteProvider.createNotesForPaginationTesting();
    given()
        .contentType(ContentType.JSON)
        .body(List.of(Tag.PERSONAL))
        .when()
        .get("/notes/tag?page=0&size=2")
        .then()
        .statusCode(200)
        .body("size()", equalTo(2))
        .body("[0].title", equalTo("Personal Note 3 - Newest"))
        .body("[1].title", equalTo("Personal Note 2"));
  }

  @Test
  void getNotesByTag_WithEmptyPage_ShouldReturnEmptyList() {
    given()
        .contentType(ContentType.JSON)
        .body(List.of(Tag.PERSONAL))
        .when()
        .get("/notes/tag?page=10&size=2")
        .then()
        .statusCode(200)
        .body("size()", equalTo(0));
  }

  @Test
  void getNotesByTag_MultipleTags_ShouldReturnUnion() {
    noteProvider.createNotesForPaginationTesting();

    given()
        .contentType(ContentType.JSON)
        .body(List.of(Tag.BUSINESS, Tag.IMPORTANT))
        .when()
        .get("/notes/tag?page=0&size=10")
        .then()
        .statusCode(200)
        .body("size()", equalTo(1))
        .body("[0].title", equalTo("Business Note 3 - Newest"));
  }

  @Test
  void getNotesByTag_EmptyTags_ReturnsAllNotes() {
    noteProvider.createTestNotes(2);

    given()
        .contentType(ContentType.JSON)
        .body(List.of())
        .when()
        .get("/notes/tag?page=0&size=10")
        .then()
        .statusCode(200)
        .body("size()", greaterThanOrEqualTo(2));
  }


  @Test
  void getNoteById_ExistingNote_ReturnsNote() {
    String noteId = noteProvider.createTestNote();

    given()
        .when()
        .get("/notes/{id}", noteId)
        .then()
        .statusCode(200)
        .body("id", equalTo(noteId))
        .body("title", equalTo("Test Note"));
  }

  @Test
  void getNoteById_NonExistingNote_ReturnsNotFound() {
    given()
        .when()
        .get("/notes/{id}", "non-existing-id")
        .then()
        .statusCode(404);
  }

  @Test
  void getNoteStatsById_ExistingNote_ReturnsWordStats() {
    String noteId = noteProvider.createNoteWithText("note is just a note");

    given()
        .when()
        .get("/notes/{id}/stats", noteId)
        .then()
        .statusCode(200)
        .body("", equalTo(Map.of(
            "note", 2,
            "is", 1,
            "just", 1,
            "a", 1
        )));
  }

  @Test
  void getNoteStatsById_NonExistingNote_ReturnsNotFound() {
    given()
        .when()
        .get("/notes/{id}/stats", "non-existing-id")
        .then()
        .statusCode(404);
  }

  @Test
  void updateNote_ExistingNote_ReturnsUpdatedNote() {
    String noteId = noteProvider.createTestNote();

    UpdateNoteRequest updateRequest = UpdateNoteRequest.builder()
        .title("Updated Title")
        .text("Updated content")
        .tags(List.of(Tag.BUSINESS))
        .build();

    given()
        .contentType(ContentType.JSON)
        .body(updateRequest)
        .when()
        .put("/notes/{id}", noteId)
        .then()
        .statusCode(200)
        .body("title", equalTo("Updated Title"))
        .body("text", equalTo("Updated content"))
        .body("tags", hasItems("BUSINESS"));
  }

  @Test
  void updateNote_PartialUpdate_ReturnsPartiallyUpdatedNote() {
    String noteId = noteProvider.createTestNote();

    UpdateNoteRequest partialRequest = UpdateNoteRequest.builder()
        .title("Only Title Updated")
        .build();

    given()
        .contentType(ContentType.JSON)
        .body(partialRequest)
        .when()
        .put("/notes/{id}", noteId)
        .then()
        .statusCode(200);
  }

  @Test
  void updateNote_NonExistingNote_ReturnsNotFound() {
    UpdateNoteRequest request = UpdateNoteRequest.builder()
        .title("New Title")
        .build();

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/notes/{id}", "non-existing-id")
        .then()
        .statusCode(404);
  }

  @Test
  void updateNote_InvalidRequest_ReturnsBadRequest() {
    String noteId = noteProvider.createTestNote();

    UpdateNoteRequest invalidRequest = UpdateNoteRequest.builder()
        .title("")
        .build();

    given()
        .contentType(ContentType.JSON)
        .body(invalidRequest)
        .when()
        .put("/notes/{id}", noteId)
        .then()
        .statusCode(400);
  }

  @Test
  void deleteNote_ExistingNote_ReturnsNoContent() {
    String noteId = noteProvider.createTestNote();

    given()
        .when()
        .delete("/notes/{id}", noteId)
        .then()
        .statusCode(204);

    given()
        .when()
        .get("/notes/{id}", noteId)
        .then()
        .statusCode(404);
  }

  @Test
  void deleteNote_NonExistingNote_ReturnsNotFound() {
    given()
        .when()
        .delete("/notes/{id}", "non-existing-id")
        .then()
        .statusCode(404);
  }
}
