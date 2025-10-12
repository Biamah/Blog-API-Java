package com.seuportfolio.blog_api.repository;

import com.seuportfolio.blog_api.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_WhenUserExists_ReturnsUser() {
        User user = User.builder()
                .username("testuser")
                .email("test@email.com")
                .password("password")
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ReturnsEmpty() {
        Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void existsByUsername_WhenUserExists_ReturnsTrue() {
        User user = User.builder()
                .username("testuser")
                .email("test@email.com")
                .password("password")
                .build();
        userRepository.save(user);

        Boolean exists = userRepository.existsByUsername("testuser");
        assertTrue(exists);
    }

    @Test
    void existsByEmail_WhenEmailExists_ReturnsTrue() {
        User user = User.builder()
                .username("testuser")
                .email("test@email.com")
                .password("password")
                .build();
        userRepository.save(user);

        Boolean exists = userRepository.existsByEmail("test@email.com");
        assertTrue(exists);
    }
}
