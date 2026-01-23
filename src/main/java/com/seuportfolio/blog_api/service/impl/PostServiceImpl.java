package com.seuportfolio.blog_api.service.impl;

import com.seuportfolio.blog_api.dto.CreatePostDTO;
import com.seuportfolio.blog_api.dto.PostDTO;
import com.seuportfolio.blog_api.entity.Post;
import com.seuportfolio.blog_api.mapper.PostMapper;
import com.seuportfolio.blog_api.repository.PostRepository;
import com.seuportfolio.blog_api.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;


    @Override
    public Page<PostDTO> getAll(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostMapper::toDTO);
    }

    @Override
    public PostDTO getById(String id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        return PostMapper.toDTO(post);
    }

    @Override
    public PostDTO create(CreatePostDTO dto) {
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(dto.getAuthor())
                .build();

        postRepository.save(post);
        return PostMapper.toDTO(post);
    }

    @Override
    public PostDTO update(String id, CreatePostDTO dto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        postRepository.save(post);
        return PostMapper.toDTO(post);
    }

    @Override
    public void delete(String id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.delete(post);
    }
}
