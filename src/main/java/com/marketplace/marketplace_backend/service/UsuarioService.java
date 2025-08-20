package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.model.Role;
import com.marketplace.marketplace_backend.model.Usuario;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Criar novo usuário
    public Usuario createUsuario(String nome, String email, String senha, Role role, String endereco) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Usuário já existe com esse email");
        }

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha(passwordEncoder.encode(senha))
                .role(role)
                .endereco(endereco)
                .build();

        return usuarioRepository.save(usuario);
    }

    // Buscar usuário por ID
    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }

    // Buscar usuário por email
    public Usuario getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
    }

    // Listar todos os usuários
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    // Atualizar usuário
    public Usuario updateUsuario(Long id, String nome, String email, String senha, Role role, String endereco) {
        Usuario usuario = getUsuarioById(id);

        if (email != null && !email.equals(usuario.getEmail())) {
            if (usuarioRepository.findByEmail(email).isPresent()) {
                throw new RuntimeException("Outro usuário já possui esse email");
            }
            usuario.setEmail(email);
        }

        if (nome != null) usuario.setNome(nome);
        if (senha != null) usuario.setSenha(passwordEncoder.encode(senha));
        if (role != null) usuario.setRole(role);
        if (endereco != null) usuario.setEndereco(endereco);

        return usuarioRepository.save(usuario);
    }

    // Deletar usuário
    public void deleteUsuario(Long id) {
        Usuario usuario = getUsuarioById(id);
        usuarioRepository.delete(usuario);
    }
}
