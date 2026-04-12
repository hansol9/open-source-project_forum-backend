package com.hansol.forum.application.service;

import com.hansol.forum.application.dto.CommentRequest;
import com.hansol.forum.domain.model.Comment;
import com.hansol.forum.domain.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private CommentRequest commentRequest;
    private Comment existingComment;

    @BeforeEach
    void setUp() {
        commentRequest = new CommentRequest();
        commentRequest.setContent("Test Comment");

        existingComment = new Comment();
        existingComment.setId(1L);
        existingComment.setContent("Existing Comment");
        existingComment.setUserId(1L);
        existingComment.setPostId(1L);
        existingComment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void create_Success() {
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(1L);
            return comment;
        });

        Comment result = commentService.create(commentRequest, 1L, 1L);

        assertNotNull(result);
        assertEquals("Test Comment", result.getContent());
        assertEquals(1L, result.getPostId());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void findByPostId_ReturnsComment() {
        when(commentRepository.findByPostId(1L)).thenReturn(List.of(existingComment));

        List<Comment> result = commentService.findByPostId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void update_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(existingComment);

        Comment result = commentService.update(1L, commentRequest, 1L);

        assertEquals("Test Comment", result.getContent());
    }

    @Test
    void update_NotOwner_ThrowsException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.update(1L, commentRequest, 999L));
    }

    @Test
    void update_NotFound_ThrowsException() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.update(99L, commentRequest, 1L));
    }

    @Test
    void delete_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment));

        commentService.delete(1L, 1L);

        verify(commentRepository).delete(existingComment);
    }

    @Test
    void delete_NotOwner_ThrowsException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.delete(1L, 999L));
    }

    @Test
    void delete_NotFound_ThrowsException() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.delete(99L, 1L));
    }

    @Test
    void countComments_ReturnsCount() {
        when(commentRepository.count()).thenReturn(20L);

        long count = commentService.countComments();

        assertEquals(20L, count);
    }

}
