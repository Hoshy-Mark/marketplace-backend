package com.marketplace.marketplace_backend.config;

import com.marketplace.marketplace_backend.service.AuthService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private final AuthService authService;

    public DataLoader(AuthService authService) {
        this.authService = authService;
    }

    @PostConstruct
    public void loadData() {
        String nome = "Administrador";
        String email = "admin@marketplace.com";
        String senha = "admin123";
        String role = "ADMIN";

        try {
            authService.createUser(nome, email, senha, role);
            System.out.println("Usuário inicial criado: " + email + "/" + senha);
        } catch (Exception e) {
            System.out.println("Usuário já existe. Ignorando criação inicial.");
        }
    }
}
