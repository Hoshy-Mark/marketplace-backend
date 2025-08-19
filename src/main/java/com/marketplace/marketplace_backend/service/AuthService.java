package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.LoginRequest;
import com.marketplace.marketplace_backend.dto.LoginResponse;
import com.marketplace.marketplace_backend.model.User;
import com.marketplace.marketplace_backend.repository.UserRepository;
import com.marketplace.marketplace_backend.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                       UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration,
                                                       CustomUserDetailsService userDetailsService) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    public LoginResponse login(LoginRequest request) {
        System.out.println("Tentando login: " + request.getUsername());

        var authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        try {
            authenticationManager.authenticate(authToken);
            System.out.println("Autenticação bem-sucedida");
        } catch (Exception e) {
            System.out.println("Falha na autenticação: " + e.getMessage());
            throw e;
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtUtils.generateToken(user);
        System.out.println("Token gerado: " + token);

        return new LoginResponse(token);
    }

    // Método auxiliar para criar usuário inicial (para teste)
    public User createUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }
}
