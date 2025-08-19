package com.marketplace.marketplace_backend.config;

import com.marketplace.marketplace_backend.model.User;
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
        // Cria um usuário inicial apenas se não existir
        String username = "admin";
        String password = "admin123";
        String role = "ADMIN";

        try {
            authService.createUser(username, password, role);
            System.out.println("Usuário inicial criado: " + username + "/" + password);
        } catch (Exception e) {
            System.out.println("Usuário já existe. Ignorando criação inicial.");
        }
    }
}
