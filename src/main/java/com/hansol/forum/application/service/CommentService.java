package com.hansol.forum.application.service;

import com.hansol.forum.application.dto.CommentRequest;
import com.hansol.forum.domain.model.Comment;
import com.hansol.forum.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment create(CommentRequest request, Long postId, Long userId) {
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment update(Long id, CommentRequest request, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to edit this comment");
        }

        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public void delete(Long id, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    public long countComments() {
        return commentRepository.count();
    }
}
