package com.marketplace.marketplace_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoriaResponse {
    private Long id;
    private String nome;
    private String descricao;
}
