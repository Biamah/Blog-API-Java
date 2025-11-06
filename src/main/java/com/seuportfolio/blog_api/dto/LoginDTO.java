package com.seuportfolio.blog_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "Username ou email é obrigatório")
    private String usernameOrEmail;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
