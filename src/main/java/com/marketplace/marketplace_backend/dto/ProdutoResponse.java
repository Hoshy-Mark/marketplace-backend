package com.marketplace.marketplace_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProdutoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer quantidadeEstoque;
    private Long categoriaId; // devolvemos o id da categoria
    private Long vendedorId;
    private String dataCriacao;
}
