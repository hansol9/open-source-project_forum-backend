package com.hansol.forum.application.service;

import com.hansol.forum.domain.model.Like;
import com.hansol.forum.domain.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public Like likePost(Long postId, Long userId) {
        log.info("User {} liking post {}", userId, postId);
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            log.warn("User {} already liked post {}", userId, postId);
            throw new IllegalArgumentException("Already liked this post");
        }

        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        like.setCreatedAt(LocalDateTime.now());
        log.info("Post {} liked successfully by user {}", postId, userId);
        return likeRepository.save(like);
    }

    public void unlikePost(Long postId, Long userId) {
        log.info("User {} unliking post {}", userId, postId);
        Like like = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new IllegalArgumentException("Like not found"));
        likeRepository.delete(like);
        log.info("Post {} unliked successfully by user{}", postId, userId);
    }

    public long countByPostId(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean isLikedByUser(Long userId, Long postId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }
}
