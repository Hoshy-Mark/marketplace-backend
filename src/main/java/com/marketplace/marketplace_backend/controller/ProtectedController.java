package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.LoginRequest;
import com.marketplace.marketplace_backend.dto.LoginResponse;
import com.marketplace.marketplace_backend.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/protected")
public class ProtectedController {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String protectedEndpoint() {
        return "Você acessou um endpoint protegido!";
    }
}
