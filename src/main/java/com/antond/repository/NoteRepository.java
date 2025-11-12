package com.antond.repository;

import com.antond.constants.Tag;
import com.antond.entity.Note;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
  List<Note> findAllByOrderByCreatedDateDesc(Pageable pageable);
  List<Note> findByTagsInOrderByCreatedDateDesc(List<Tag> tags, Pageable pageable);
}
