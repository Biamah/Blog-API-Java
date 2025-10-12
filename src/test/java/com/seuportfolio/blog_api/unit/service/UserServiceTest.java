package com.seuportfolio.blog_api.unit.service;

import com.seuportfolio.blog_api.dto.CreateUserDTO;
import com.seuportfolio.blog_api.dto.UserDTO;
import com.seuportfolio.blog_api.entity.User;
import com.seuportfolio.blog_api.repository.UserRepository;
import com.seuportfolio.blog_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;
    private CreateUserDTO createUserDto;
    private User user;

    @BeforeEach
    void setUp() {
        createUserDto = CreateUserDTO.builder()
                .username("testuser")
                .email("test@email.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@email.com")
                .password("encodedPassword123")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso quando dados são válidos")
    void createUser_WithValidData_ReturnsUserDto() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = service.createUser(createUserDto);

        assertNotNull(result);
        assertEquals(1l, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@email.com", result.getEmail());

        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("test@email.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome de usuário já existe")
    void createUser_WithExistingUsername_ThrowsException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.createUser(createUserDto);
        });

        assertEquals("Username já está em uso", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void createUser_WithExistingEmail_ThrowsException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.createUser(createUserDto);
        });

        assertEquals("Email já está em uso", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve codificar a senha ao criar usuário")
    void createUser_PasswordIsEncoded() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        service.createUser(createUserDto);

        verify(passwordEncoder, times(1)).encode("password123");
    }
}
