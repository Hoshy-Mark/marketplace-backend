package com.marketplace.marketplace_backend.repository;

import com.marketplace.marketplace_backend.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Buscar por categoria
    Page<Produto> findByCategoria_Id(Long categoriaId, Pageable pageable);

    // Buscar por preço (menor ou igual ao max)
    Page<Produto> findByPrecoLessThanEqual(BigDecimal precoMax, Pageable pageable);

    // Buscar por categoria + preço
    Page<Produto> findByCategoria_IdAndPrecoLessThanEqual(Long categoriaId, BigDecimal precoMax, Pageable pageable);
}
