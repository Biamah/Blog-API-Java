package com.seuportfolio.blog_api.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class PostDTO {
    private String id;
    private String title;
    private String content;
    private String author;
    private Instant createdAt;
    private Instant updatedAt;
}
