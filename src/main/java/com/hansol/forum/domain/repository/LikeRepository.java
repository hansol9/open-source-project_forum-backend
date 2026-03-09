package com.hansol.forum.domain.repository;

import com.hansol.forum.domain.model.Like;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    List<Like> findByPostId(Long postId);
    long countByPostId(Long postId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
}
