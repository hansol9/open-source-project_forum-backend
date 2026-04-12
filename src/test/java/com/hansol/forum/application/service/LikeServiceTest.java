package com.hansol.forum.application.service;

import com.hansol.forum.domain.model.Like;
import com.hansol.forum.domain.repository.LikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    void likePost_Success() {
        when(likeRepository.existsByUserIdAndPostId(1L, 1L)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> {
            Like like = invocation.getArgument(0);
            like.setId(1L);
            return like;
        });

        Like result = likeService.likePost(1L, 1L);

        assertNotNull(result);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void likePost_AlreadyLiked_ThrowsException() {
        when(likeRepository.existsByUserIdAndPostId(1L, 1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> likeService.likePost(1L, 1L));
    }

    @Test
    void unlikePost_Success() {
        Like like = new Like();
        like.setId(1L);

        when(likeRepository.findByUserIdAndPostId(1L, 1L)).thenReturn(Optional.of(like));

        likeService.unlikePost(1L, 1L);

        verify(likeRepository).delete(like);
    }

    @Test
    void unlikePost_NotFount_ThrowsException() {
        when(likeRepository.findByUserIdAndPostId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> likeService.unlikePost(1L, 1L));
    }

    @Test
    void countByPostId_ReturnsCount() {
        when(likeRepository.countByPostId(1L)).thenReturn(5L);

        long count = likeService.countByPostId(1L);

        assertEquals(5L, count);
    }

    @Test
    void isLikedByUser_True() {
        when(likeRepository.existsByUserIdAndPostId(1L, 1L)).thenReturn(true);

        assertTrue(likeService.isLikedByUser(1L, 1L));
    }

    @Test
    void isLikedByUser_False() {
        when(likeRepository.existsByUserIdAndPostId(1L, 1L)).thenReturn(false);

        assertFalse(likeService.isLikedByUser(1L, 1L));
    }
}
