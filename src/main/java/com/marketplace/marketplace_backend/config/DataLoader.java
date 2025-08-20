package com.marketplace.marketplace_backend.config;

import com.marketplace.marketplace_backend.model.Role;
import com.marketplace.marketplace_backend.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private final UsuarioService usuarioService;

    public DataLoader(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostConstruct
    public void loadData() {
        String nome = "Administrador";
        String email = "admin@marketplace.com";
        String senha = "admin123";
        Role role = Role.ADMIN;
        String endereco = "Rua Admin, 1";

        try {
            usuarioService.createUsuario(nome, email, senha, role, endereco);
            System.out.println("Usuário inicial criado: " + email + "/" + senha);
        } catch (Exception e) {
            System.out.println("Usuário já existe. Ignorando criação inicial.");
        }
    }
}
