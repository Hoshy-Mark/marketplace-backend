package com.marketplace.marketplace_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequest {
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer quantidadeEstoque;
    private Long categoriaId; // vem do front
}
