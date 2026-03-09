package com.hansol.forum.domain.repository;

import com.hansol.forum.domain.model.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);
    List<Post> findAllByOrderByCreatedAtDesc();
}
