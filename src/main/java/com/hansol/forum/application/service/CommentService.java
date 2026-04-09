package com.hansol.forum.application.service;

import com.hansol.forum.application.dto.CommentRequest;
import com.hansol.forum.domain.model.Comment;
import com.hansol.forum.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment create(CommentRequest request, Long postId, Long userId) {
        log.info("Create comment on post {} by user {}", postId, userId);
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully with id: {}", savedComment.getId());
        return savedComment;
    }

    public List<Comment> findByPostId(Long postId) {
        log.debug("Finding comments for post: {}", postId);
        return commentRepository.findByPostId(postId);
    }

    public Comment update(Long id, CommentRequest request, Long userId) {
        log.info("Updating comment {} by user {}", id, userId);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            log.warn("User {} not authorized to edit comment {}", userId, id);
            throw new IllegalArgumentException("Not authorized to edit this comment");
        }

        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        log.info("Comment {} updated successfully", id);
        return commentRepository.save(comment);
    }

    public void delete(Long id, Long userId) {
        log.info("Deleting comment {} by user {}", id, userId);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            log.warn("User {} not authorized to delete comment {}", userId, id);
            throw new IllegalArgumentException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
        log.info("Comment {} deleted successfully", id);
    }

    public long countComments() {
        return commentRepository.count();
    }
}
