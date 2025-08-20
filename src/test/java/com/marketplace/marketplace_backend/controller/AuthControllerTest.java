package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.LoginRequest;
import com.marketplace.marketplace_backend.dto.LoginResponse;
import com.marketplace.marketplace_backend.dto.RegisterRequest;
import com.marketplace.marketplace_backend.dto.RegisterResponse;
import com.marketplace.marketplace_backend.model.Role;
import com.marketplace.marketplace_backend.model.Usuario;
import com.marketplace.marketplace_backend.service.AuthService;
import com.marketplace.marketplace_backend.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Teste de login bem-sucedido
    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("teste@teste.com");
        request.setPassword("senha123");

        LoginResponse loginResponse = new LoginResponse("token123", "COMPRADOR");
        when(authService.login(request)).thenReturn(loginResponse);

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loginResponse, response.getBody());
    }

    // Teste de login com falha
    @Test
    void testLoginFailure() {
        LoginRequest request = new LoginRequest();
        request.setEmail("teste@teste.com");
        request.setPassword("senhaerrada");

        when(authService.login(request)).thenThrow(new RuntimeException("Credenciais inválidas"));

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas", response.getBody());
    }

    // Teste de registro bem-sucedido
    @Test
    void testRegisterSuccess() {
        // Gera email único para evitar conflito
        String uniqueEmail = "joao" + System.currentTimeMillis() + "@teste.com";

        RegisterRequest request = new RegisterRequest();
        request.setNome("João");
        request.setEmail(uniqueEmail);
        request.setSenha("senha123");
        request.setRole("COMPRADOR");
        request.setEndereco("Rua A, 123");

        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome(request.getNome())
                .email(request.getEmail())
                .role(Role.COMPRADOR)
                .endereco(request.getEndereco())
                .dataCriacao(LocalDateTime.now())
                .build();

        // Mock do serviço
        when(usuarioService.createUsuario(
                anyString(), anyString(), anyString(), any(Role.class), anyString()
        )).thenReturn(usuario);

        ResponseEntity<?> response = authController.register(request);

        // Verifica se retornou CREATED (201)
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof RegisterResponse);

        RegisterResponse registerResponse = (RegisterResponse) response.getBody();
        assertEquals(usuario.getId(), registerResponse.getId());
        assertEquals(usuario.getNome(), registerResponse.getNome());
        assertEquals(usuario.getEmail(), registerResponse.getEmail());
        assertEquals(usuario.getRole().name(), registerResponse.getRole());
        assertEquals(usuario.getEndereco(), registerResponse.getEndereco());
    }

    // Teste de registro com role inválida
    @Test
    void testRegisterInvalidRole() {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Maria");
        request.setEmail("maria@teste.com");
        request.setSenha("senha123");
        request.setRole("INVALID_ROLE");

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Role inválida. Valores válidos: COMPRADOR, VENDEDOR, ADMIN", response.getBody());
    }
}
