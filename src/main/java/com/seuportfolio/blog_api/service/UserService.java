package com.seuportfolio.blog_api.service;

import com.seuportfolio.blog_api.dto.CreateUserDTO;
import com.seuportfolio.blog_api.dto.UserDTO;
import com.seuportfolio.blog_api.entity.User;
import com.seuportfolio.blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO createUser(CreateUserDTO createUserDTO) {
        if(userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new IllegalArgumentException("Username já está em uso");
        }

        if(userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        User user = User.builder()
                .username(createUserDTO.getUsername())
                .email(createUserDTO.getEmail())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    private UserDTO convertToDTO(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt().format(formatter))
                .build();
    }
}
