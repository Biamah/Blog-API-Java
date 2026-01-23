package com.seuportfolio.blog_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.blog_api.dto.CreatePostDTO;
import com.seuportfolio.blog_api.dto.PostDTO;
import com.seuportfolio.blog_api.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.Instant;
import java.util.Collections;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private PostService postService;

    private PostDTO postDTO;

    @BeforeEach
    void setUp() {
        postDTO = new PostDTO();
        postDTO.setId("1");
        postDTO.setTitle("Test Post");
        postDTO.setContent("This is a test post.");
        postDTO.setAuthor("Test Author");
        postDTO.setCreatedAt(Instant.now());
        postDTO.setUpdatedAt(Instant.now());
    }

    @Test
    void getAll() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        when(postService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(postDTO), pageable, 1));

        mockMvc.perform(get("/api/posts").param("page", "0").param("size", "10").param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"));
    }

    @Test
    void getById() throws Exception {
        when(postService.getById("1")).thenReturn(postDTO);

        mockMvc.perform(get("/api/posts/" + postDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Post"));
    }

    @Test
    void create() throws Exception {
        PostDTO toCreate = new PostDTO();
        toCreate.setTitle("New Post");
        toCreate.setContent("This is a new post.");
        toCreate.setAuthor("New Author");

        when(postService.create(any(CreatePostDTO.class))).thenReturn(postDTO);

        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postDTO.getId()));
    }

    @Test
    void update() throws Exception {
        PostDTO update = new PostDTO();
        update.setTitle("Updated Post");
        update.setContent("This is an updated post.");
        update.setAuthor("Updated Author");

        when(postService.update(eq(postDTO.getId()), any(CreatePostDTO.class))).thenReturn(postDTO);

        mockMvc.perform(put("/api/posts/" + postDTO.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postDTO.getId()));
    }

    @Test
    void delete() throws Exception {
        doNothing().when(postService).delete(postDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/" + postDTO.getId()))
                .andExpect(status().isNoContent());

        verify(postService, times(1)).delete(postDTO.getId());
    }
}