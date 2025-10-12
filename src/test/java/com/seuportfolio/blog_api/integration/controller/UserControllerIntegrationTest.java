package com.seuportfolio.blog_api.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.blog_api.dto.CreateUserDTO;
import com.seuportfolio.blog_api.dto.UserDTO;
import com.seuportfolio.blog_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void registerUser_WithValidData_ReturnsCreated() throws Exception {
        CreateUserDTO createUserDTO = new CreateUserDTO("tester", "test@email.com", "123456");
        String requestBody = objectMapper.writeValueAsString(createUserDTO);

        when(userService.createUser(any(CreateUserDTO.class)))
                .thenReturn(UserDTO.builder()
                        .id(1L)
                        .username("tester")
                        .email("test@email.com")
                        .createdAt("15/12/2023 10:00")
                        .build());

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("tester"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void registerUser_WithInvalidData_ReturnsBadRequest() throws Exception {
        CreateUserDTO createUserDTO = new CreateUserDTO("tester", "invalid-email", "123");
        String requestBody = objectMapper.writeValueAsString(createUserDTO);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WithEmptyBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
