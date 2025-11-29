package com.seuportfolio.blog_api.unit.controller;

import com.seuportfolio.blog_api.controller.AuthController;
import com.seuportfolio.blog_api.dto.*;
import com.seuportfolio.blog_api.service.AuthService;
import com.seuportfolio.blog_api.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class AuthControllerTest {
    @Mock
    private AuthService authService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // signUp
    @Test
    void registerUser_ShouldReturnUserDTO() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        UserDTO userDTO = new UserDTO();

        when(authService.singUp(createUserDTO)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = authController.registerUser(createUserDTO);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(userDTO);
        verify(authService, times(1)).singUp(createUserDTO);
    }

    // refreshToken
    @Test
    void refreshToken_ShouldReturnNewJwtResponseDTO() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("oldToken");

        JwtResponseDTO jwtResponse = new JwtResponseDTO();
        when(authService.refreshToken("oldToken")).thenReturn(jwtResponse);

        ResponseEntity<JwtResponseDTO> response = authController.refreshToken(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(jwtResponse);
        verify(authService, times(1)).refreshToken("oldToken");
    }

    // logIn
    @Test
    void authenticateUser_ShouldReturnJwtResponseDTO() {
        LoginDTO loginDTO = new LoginDTO();
        JwtResponseDTO jwtResponse = new JwtResponseDTO();
        when(authService.signIn(loginDTO)).thenReturn(jwtResponse);

        ResponseEntity<JwtResponseDTO> response = authController.authenticateUser(loginDTO);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(jwtResponse);
        verify(authService, times(1)).signIn(loginDTO);
    }

    //logOut
    @Test
    void logoutUser_ShouldReturnSuccessMessage() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("token123");

        ResponseEntity<String> response = authController.logoutUser(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Logout realizado com sucesso!");
        verify(authService, times(1)).singOut("token123");
    }
}
