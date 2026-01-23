package com.seuportfolio.blog_api.service;

import com.seuportfolio.blog_api.dto.CreatePostDTO;
import com.seuportfolio.blog_api.dto.PostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<PostDTO> getAll(Pageable pageable);

    PostDTO getById(String id);

    PostDTO create(CreatePostDTO dto);

    PostDTO update(String id, CreatePostDTO dto);

    void delete(String id);
}
