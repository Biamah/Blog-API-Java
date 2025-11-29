package com.seuportfolio.blog_api.config;

import com.seuportfolio.blog_api.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfig.class, CustomUserDetailsService.class})
public class SecurityConfigTest {
    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void beansShouldBePresent() {
        UserDetailsService userDetailsService = ctx.getBean(UserDetailsService.class);

        assertNotNull(ctx.getBean(SecurityConfig.class));
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
        assertNotNull(authenticationProvider);
        assertNotNull(authenticationManager);
        assertNotNull(corsConfigurationSource);
        assertNotNull(securityFilterChain);
        assertNotNull(userDetailsService);
    }

    @Test
    void optionsRequestShouldBeAllowedByCors() throws Exception {
        mockMvc.perform(options("/api/any")
            .header("Origin", "http://example.com")
            .header("Access-Control-Request-Method", "GET"))
        .andExpect(status().isOk())
        .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void permittedEndpointsShouldPassSecurityAndReturnNotFound() throws Exception {
        mockMvc.perform(post("/api/auth/nao-existe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/users/nao-existe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/h2-console/nao-existe"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/error"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError());
    }

}
