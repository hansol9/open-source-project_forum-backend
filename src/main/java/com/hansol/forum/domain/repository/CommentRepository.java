package com.hansol.forum.domain.repository;

import com.hansol.forum.domain.model.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByPostUserId(Long userId);
}
