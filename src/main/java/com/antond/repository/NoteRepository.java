package com.antond.repository;

import com.antond.constants.Tag;
import com.antond.entity.Note;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing database operations on Note entities. Extends MongoRepository
 * to provide CRUD operations and custom query methods for accessing and managing notes in the
 * MongoDB database.
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

  /**
   * Finds all notes ordered by creation date in descending order (newest first) with pagination
   * support. This method is typically used for displaying recent notes first in list views.
   *
   * @param pageable the pagination information including page number and size
   * @return a list of notes sorted by creation date descending, limited by pagination
   */
  List<Note> findAllByOrderByCreatedDateDesc(Pageable pageable);

  /**
   * Finds notes that contain all the specified tags using a MongoDB query. This method performs an
   * $all query to match notes that have every tag in the provided list. Results are paginated and
   * can include additional sorting via the Pageable parameter.
   *
   * @param tags     the list of tags that must all be present in the note's tags collection
   * @param pageable the pagination information including page number, size, and optional sorting
   * @return a list of notes that contain all the specified tags, paginated according to pageable
   */
  @Query("{ 'tags' : { $all: ?0 } }")
  List<Note> findByTagsContainingAll(List<Tag> tags, Pageable pageable);
}
