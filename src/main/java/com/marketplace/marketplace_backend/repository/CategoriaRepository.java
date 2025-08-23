package com.marketplace.marketplace_backend.repository;

import com.marketplace.marketplace_backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
