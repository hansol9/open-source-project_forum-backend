package com.hansol.forum.application.service;

import com.hansol.forum.application.dto.PostRequest;
import com.hansol.forum.domain.model.Post;
import com.hansol.forum.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post create(PostRequest request, Long userId) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUserId(userId);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> findAll() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> findByUserId(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    public Post update(Long id, PostRequest request, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to edit this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void delete(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    public long countPost() {
        return postRepository.count();
    }
}
