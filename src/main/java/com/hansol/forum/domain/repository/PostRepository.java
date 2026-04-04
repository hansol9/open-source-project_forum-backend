package com.hansol.forum.domain.repository;

import com.hansol.forum.domain.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);
    List<Post> findAllByOrderByCreatedAtDesc();
}
