package com.seuportfolio.blog_api.controller;

import com.seuportfolio.blog_api.dto.CreatePostDTO;
import com.seuportfolio.blog_api.dto.PostDTO;
import com.seuportfolio.blog_api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getAll(
            @PageableDefault(sort = "createdAt", size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PostDTO> create(@Valid @RequestBody CreatePostDTO dto) {
        return ResponseEntity.ok(postService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> update(@PathVariable String id, @Valid @RequestBody CreatePostDTO dto) {
        return ResponseEntity.ok(postService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PostDTO> delete(@PathVariable String id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
