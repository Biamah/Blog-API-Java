package com.seuportfolio.blog_api.service;

import com.seuportfolio.blog_api.dto.CreateUserDTO;
import com.seuportfolio.blog_api.dto.JwtResponseDTO;
import com.seuportfolio.blog_api.dto.LoginDTO;
import com.seuportfolio.blog_api.dto.UserDTO;
import com.seuportfolio.blog_api.entity.RefreshToken;
import com.seuportfolio.blog_api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;

    public UserDTO singUp(CreateUserDTO createUserDTO) {
        return userService.createUser(createUserDTO);
    }

    public JwtResponseDTO signIn(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsernameOrEmail(),
                        loginDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);

        User user = userService.findByUsernameOrEmail(loginDTO.getUsernameOrEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return JwtResponseDTO.builder()
                .acessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .type("Bearer")
                .build();
    }

    public JwtResponseDTO refreshToken(String requestRefreshToken) {
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
                    String acessToken = jwtService.generateToken(userDetails);
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

                    return JwtResponseDTO.builder()
                            .acessToken(acessToken)
                            .refreshToken(newRefreshToken.getToken())
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token inválido!"));
    }

    public void singOut(String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
    }
}
