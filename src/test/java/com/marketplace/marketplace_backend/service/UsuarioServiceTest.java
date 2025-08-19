package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.model.Usuario;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    @Test
    void testCreateUsuario_Success() {
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hashed123456");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuario = usuarioService.createUsuario("Teste", "teste@email.com", "123456", "USER", "Rua A");

        assertEquals("Teste", usuario.getNome());
        assertEquals("hashed123456", usuario.getSenha());
        assertEquals("USER", usuario.getRole());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testCreateUsuario_EmailExists() {
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(new Usuario()));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                usuarioService.createUsuario("Teste", "teste@email.com", "123456", "USER", "Rua A")
        );
        assertEquals("Usuário já existe com esse email", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testGetUsuarioById_Success() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario found = usuarioService.getUsuarioById(1L);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetUsuarioById_NotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.getUsuarioById(1L));
        assertEquals("Usuário não encontrado com id: 1", ex.getMessage());
    }

    @Test
    void testGetUsuarioByEmail_Success() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(usuario));

        Usuario found = usuarioService.getUsuarioByEmail("teste@email.com");
        assertEquals("teste@email.com", found.getEmail());
    }

    @Test
    void testGetUsuarioByEmail_NotFound() {
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.getUsuarioByEmail("teste@email.com"));
        assertEquals("Usuário não encontrado com email: teste@email.com", ex.getMessage());
    }

    @Test
    void testUpdateUsuario_Success() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario updated = usuarioService.updateUsuario(1L, "Novo Nome", "novo@email.com", "123456", "ADMIN", "Rua B");

        assertEquals("Novo Nome", updated.getNome());
        assertEquals("novo@email.com", updated.getEmail());
        assertEquals("ADMIN", updated.getRole());
        verify(usuarioRepository, times(1)).save(updated);
    }

    @Test
    void testUpdateUsuario_NotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                usuarioService.updateUsuario(1L, "Nome", "email", "123", "USER", "Rua A")
        );
        assertEquals("Usuário não encontrado com id: 1", ex.getMessage());
    }

    @Test
    void testDeleteUsuario_Success() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.deleteUsuario(1L);

        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    void testDeleteUsuario_NotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.deleteUsuario(1L));
        assertEquals("Usuário não encontrado com id: 1", ex.getMessage());
    }
}
