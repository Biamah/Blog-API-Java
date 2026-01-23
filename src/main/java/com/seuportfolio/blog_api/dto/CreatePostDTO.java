package com.seuportfolio.blog_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePostDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String author;
}
