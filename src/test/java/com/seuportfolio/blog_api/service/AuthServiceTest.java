package com.seuportfolio.blog_api.service;

import com.seuportfolio.blog_api.dto.CreateUserDTO;
import com.seuportfolio.blog_api.dto.JwtResponseDTO;
import com.seuportfolio.blog_api.dto.LoginDTO;
import com.seuportfolio.blog_api.dto.UserDTO;
import com.seuportfolio.blog_api.entity.RefreshToken;
import com.seuportfolio.blog_api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
        authService = new AuthService(userService, authenticationManager, jwtService, refreshTokenService, userDetailsService);
    }

    @Test
    void singUp() {
        CreateUserDTO dto = new CreateUserDTO();
        UserDTO returned = new UserDTO(1l, "pessoaTeste", "email@test.com", null);

        when(userService.createUser(dto)).thenReturn(returned);

        UserDTO response = authService.singUp(dto);

        verify(userService).createUser(dto);
        assertEquals(returned, response);
    }

    @Test
    void signIn() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsernameOrEmail("pessoaTeste");
        loginDTO.setPassword("senha123");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        when(jwtService.generateToken(userDetails)).thenReturn("jwtToken");

        User user = new User();
        user.setId(1L);
        user.setUsername("pessoaTeste");
        user.setEmail("email@test.com");

        when(userService.findByUsernameOrEmail("pessoaTeste")).thenReturn(Optional.of(user));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh123");
        refreshToken.setUser(user);

        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        JwtResponseDTO response = authService.signIn(loginDTO);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(userDetails);
        verify(userService).findByUsernameOrEmail("pessoaTeste");
        verify(refreshTokenService).createRefreshToken(user);

        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());

        assertEquals("jwtToken", response.getAcessToken());
        assertEquals("refresh123", response.getRefreshToken());
        assertEquals(1L, response.getId());
        assertEquals("pessoaTeste", response.getUsername());
        assertEquals("email@test.com", response.getEmail());
    }

    @Test
    void refreshToken() {
        String oldToken = "token-abc";

        User user = new User();
        user.setId(1L);
        user.setUsername("pessoaTeste");
        user.setEmail("email@test.com");

        RefreshToken storedToken = new RefreshToken();
        storedToken.setToken(oldToken);
        storedToken.setUser(user);

        when(refreshTokenService.findByToken(oldToken)).thenReturn(Optional.of(storedToken));
        when(refreshTokenService.verifyExpiration(storedToken)).thenReturn(storedToken);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("pessoaTeste")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("newJwtToken");

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("newRefreshToken");
        newRefreshToken.setUser(user);

        when(refreshTokenService.createRefreshToken(user)).thenReturn(newRefreshToken);

        JwtResponseDTO response = authService.refreshToken(oldToken);

        verify(refreshTokenService).findByToken(oldToken);
        verify(refreshTokenService).verifyExpiration(storedToken);
        verify(userDetailsService).loadUserByUsername("pessoaTeste");
        verify(jwtService).generateToken(userDetails);
        verify(refreshTokenService).createRefreshToken(user);

        assertEquals("newJwtToken", response.getAcessToken());
        assertEquals("newRefreshToken", response.getRefreshToken());
        assertEquals(1L, response.getId());
        assertEquals("pessoaTeste", response.getUsername());
        assertEquals("email@test.com", response.getEmail());
    }

    @Test
    void refreshTokenInvalidToken() {
        when(refreshTokenService.findByToken("invalidToken")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            authService.refreshToken("invalidToken");
        });
    }

    @Test
    void singOut() {
        authService.singOut("someRefreshToken");
        verify(refreshTokenService).deleteByToken("someRefreshToken");
    }
}