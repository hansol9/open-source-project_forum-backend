package com.hansol.forum.application.service;

import com.hansol.forum.application.dto.PostRequest;
import com.hansol.forum.domain.model.Post;
import com.hansol.forum.domain.repository.PostRepository;
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
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private PostRequest postRequest;
    private Post existingPost;

    @BeforeEach
    void setUp() {
        postRequest = new PostRequest();
        postRequest.setTitle("Test Title");
        postRequest.setContent("Test Content");

        existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setTitle("Existing Title");
        existingPost.setContent("Existing Content");
        existingPost.setUserId(1L);
        existingPost.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void create_Success() {
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(1L);
            return post;
        });

        Post result = postService.create(postRequest, 1L);

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Content", result.getContent());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void findById_Found() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        Optional<Post> result = postService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Existing Title", result.get().getTitle());
    }

    @Test
    void findById_NotFound() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Post> result = postService.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ReturnsPosts() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(existingPost));

        List<Post> result = postService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findByUserId_ReturnsPost() {
        when(postRepository.findByUserId(1L)).thenReturn(List.of(existingPost));

        List<Post> result = postService.findByUserId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void update_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenReturn(existingPost);

        Post result = postService.update(1L, postRequest, 1L);

        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Content", result.getContent());
    }

    @Test
    void update_NotOwner_ThrowsException() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        assertThrows(IllegalArgumentException.class,
                () -> postService.update(1L, postRequest, 999L));
    }

    @Test
    void update_NotFound_ThrowsException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> postService.update(99L, postRequest, 1L));
    }

    @Test
    void delete_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        postService.delete(1L, 1L);

        verify(postRepository).delete(existingPost);
    }

    @Test
    void delete_NotOwner_ThrowsException() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        assertThrows(IllegalArgumentException.class,
                () -> postService.delete(1L, 999L));
    }

    @Test
    void delete_NotFound_ThrowsException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> postService.delete(99L, 1L));
    }

    @Test
    void countPosts_ReturnsCount() {
        when(postRepository.count()).thenReturn(10L);

        long count = postService.countPosts();

        assertEquals(10L, count);
    }
}
