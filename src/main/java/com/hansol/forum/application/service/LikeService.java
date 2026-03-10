package com.hansol.forum.application.service;

import com.hansol.forum.domain.model.Like;
import com.hansol.forum.domain.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public Like likePost(Long postId, Long userId) {
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new IllegalArgumentException("Already liked this post");
        }

        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        like.setCreatedAt(LocalDateTime.now());
        return likeRepository.save(like);
    }

    public void unlikePost(Long postId, Long userId) {
        Like like = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new IllegalArgumentException("Like not found"));
        likeRepository.delete(like);
    }

    public long countByPostId(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean isLikedByUser(Long userId, Long postId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }
}
