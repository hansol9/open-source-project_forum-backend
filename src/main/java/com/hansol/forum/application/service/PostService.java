package com.hansol.forum.application.service;

import com.hansol.forum.application.dto.PostRequest;
import com.hansol.forum.domain.model.Post;
import com.hansol.forum.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post create(PostRequest request, Long userId) {
        log.info("Creating new post: '{}' by user {}", request.getTitle(), userId);
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUserId(userId);
        post.setCreatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        log.info("Post created successfully with id: {}", savedPost.getId());
        return savedPost;
    }

    public Optional<Post> findById(Long id) {
        log.debug("Finding post by id: {}", id);
        return postRepository.findById(id);
    }

    public List<Post> findAll() {
        log.debug("Finding all posts");
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> findByUserId(Long userId) {
        log.debug("Finding posts by user id: {}", userId);
        return postRepository.findAllByUserId(userId);
    }

    public Post update(Long id, PostRequest request, Long userId) {
        log.info("Updating post {} by user {}", id, userId);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            log.warn("User {} not authorized to edit post {}", userId, id);
            throw new IllegalArgumentException("Not authorized to edit this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        log.info("Post {} updated successfully", id);
        return postRepository.save(post);
    }

    public void delete(Long id, Long userId) {
        log.info("Deleting post {} by user {}", id, userId);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            log.warn("User {} not authorized to delete post {}", userId, id);
            throw new IllegalArgumentException("Not authorized to delete this post");
        }

        postRepository.delete(post);
        log.info("Post {} deleted successfully", id);
    }

    public long countPosts() {
        return postRepository.count();
    }
}
