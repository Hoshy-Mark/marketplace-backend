package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.dto.LoginRequest;
import com.marketplace.marketplace_backend.dto.LoginResponse;
import com.marketplace.marketplace_backend.model.Usuario;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import com.marketplace.marketplace_backend.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        return new LoginResponse(token, usuario.getRole());
    }

    public void createUser(String nome, String email, String senha, String role) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Usuário já existe!");
        }

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha(passwordEncoder.encode(senha))
                .role(role)
                .build();

        usuarioRepository.save(usuario);
    }
}
