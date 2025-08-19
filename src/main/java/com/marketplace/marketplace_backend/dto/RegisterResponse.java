package com.marketplace.marketplace_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String nome;
    private String email;
    private String role;
    private String endereco;
    private String dataCriacao;
}
