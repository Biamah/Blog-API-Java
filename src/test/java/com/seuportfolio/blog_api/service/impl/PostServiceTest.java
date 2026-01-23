package com.seuportfolio.blog_api.service.impl;

import com.seuportfolio.blog_api.dto.CreatePostDTO;
import com.seuportfolio.blog_api.dto.PostDTO;
import com.seuportfolio.blog_api.dto.UpdatePostDTO;
import com.seuportfolio.blog_api.entity.Post;
import com.seuportfolio.blog_api.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Post entity;
    private PostDTO dto;
    private CreatePostDTO createDto;
    private CreatePostDTO updateDto;

    @BeforeEach
    void setUp() {
        dto = new PostDTO();
        dto.setId("1");
        dto.setTitle("Sample Title");
        dto.setContent("Sample Content");
        dto.setAuthor("Author Name");
        dto.setCreatedAt(Instant.now());
        dto.setUpdatedAt(Instant.now());

        entity = new Post();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setAuthor(dto.getAuthor());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        createDto = new CreatePostDTO();
        createDto.setTitle("Título");
        createDto.setContent("Conteúdo");
        createDto.setAuthor("Autor");

        updateDto = new CreatePostDTO();
        updateDto.setTitle("Título atualizado");
        updateDto.setContent("Conteúdo atualizado");
    }

    @Test
    void getAll_shouldReturnPageOfPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(postRepository.findAll(pageable)).thenReturn(page);

        Page<PostDTO> result = postService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto.getTitle(), result.getContent().get(0).getTitle());

        verify(postRepository).findAll(pageable);
    }

    @Test
    void getById_shouldReturnPostDTO_whenPostExists() {
        when(postRepository.findById("1")).thenReturn(Optional.of(entity));

        PostDTO result = postService.getById("1");

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());

        verify(postRepository).findById("1");
    }

    @Test
    void getById_shouldThrowException_whenPostDoesNotExist() {
        when(postRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> postService.getById("1"));

        verify(postRepository).findById("1");
    }

    @Test
    void create_shouldSavePostAndReturnDTO() {
        when(postRepository.save(any(Post.class))).thenReturn(entity);

        PostDTO result = postService.create(createDto);

        assertNotNull(result);
        assertEquals(createDto.getTitle(), result.getTitle());
        assertEquals(createDto.getContent(), result.getContent());
        assertEquals(createDto.getAuthor(), result.getAuthor());

        verify(postRepository).save(any(Post.class));
    }

    @Test
    void update_shouldUpdatePost_whenPostExists() {
        when(postRepository.findById("1")).thenReturn(Optional.of(entity));
        when(postRepository.save(any(Post.class))).thenReturn(entity);

        PostDTO result = postService.update("1", updateDto);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());

        Post savedPost = captor.getValue();

        assertEquals(updateDto.getTitle(), savedPost.getTitle());
        assertEquals(updateDto.getContent(), savedPost.getContent());
        assertNotNull(result);

        verify(postRepository).findById("1");
    }

    @Test
    void update_shouldThrowException_whenPostDoesNotExist() {
        when(postRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> postService.update("1", updateDto));

        verify(postRepository).findById("1");
        verify(postRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeletePost_whenPostExists() {
        when(postRepository.findById("1")).thenReturn(Optional.of(entity));

        postService.delete("1");

        verify(postRepository).findById("1");
        verify(postRepository).delete(entity);
    }

    @Test
    void delete_shouldThrowException_whenPostDoesNotExist() {
        when(postRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> postService.delete("1"));

        verify(postRepository).findById("1");
        verify(postRepository, never()).delete(any());
    }
}
