package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.LoginRequest;
import com.marketplace.marketplace_backend.dto.LoginResponse;
import com.marketplace.marketplace_backend.dto.RegisterRequest;
import com.marketplace.marketplace_backend.dto.RegisterResponse;
import com.marketplace.marketplace_backend.model.Role;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Converte a role do request para o enum Role
            Role role;
            if (request.getRole() != null) {
                try {
                    role = Role.valueOf(request.getRole().toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Role inválida. Valores válidos: COMPRADOR, VENDEDOR, ADMIN");
                }
            } else {
                role = Role.COMPRADOR; // valor padrão se não passar role
            }

            Usuario usuario = usuarioService.createUsuario(
                    request.getNome(),
                    request.getEmail(),
                    request.getSenha(),
                    role,
                    request.getEndereco()
            );

            RegisterResponse response = new RegisterResponse(
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getRole().name(),
                    usuario.getEndereco(),
                    usuario.getDataCriacao().toString()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
