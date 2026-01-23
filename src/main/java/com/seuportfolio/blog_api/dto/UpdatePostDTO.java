package com.seuportfolio.blog_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePostDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
