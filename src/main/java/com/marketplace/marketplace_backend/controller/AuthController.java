package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.LoginRequest;
import com.marketplace.marketplace_backend.dto.LoginResponse;
import com.marketplace.marketplace_backend.dto.RegisterRequest;
import com.marketplace.marketplace_backend.model.Usuario;
import com.marketplace.marketplace_backend.service.UsuarioService;
import com.marketplace.marketplace_backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String role = request.getRole() != null ? request.getRole() : "USER";

            Usuario usuario = usuarioService.createUsuario(
                    request.getNome(),
                    request.getEmail(),
                    request.getSenha(),
                    role,
                    request.getEndereco()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
