package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.LoginRequest;
import com.marketplace.marketplace_backend.dto.LoginResponse;
import com.marketplace.marketplace_backend.dto.RegisterRequest;
import com.marketplace.marketplace_backend.dto.RegisterResponse;
import com.marketplace.marketplace_backend.model.Role;
import com.marketplace.marketplace_backend.model.Usuario;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import com.marketplace.marketplace_backend.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        String token = jwtUtils.generateToken(usuario);
        return new LoginResponse(token, usuario.getRole().name());
    }

    public RegisterResponse register(RegisterRequest request) {
        // Validação da role
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role inválida. Valores válidos: COMPRADOR, VENDEDOR, ADMIN");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(role);
        usuario.setEndereco(request.getEndereco());
        usuarioRepository.save(usuario);

        return new RegisterResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name(),
                usuario.getEndereco(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        );
    }
}
