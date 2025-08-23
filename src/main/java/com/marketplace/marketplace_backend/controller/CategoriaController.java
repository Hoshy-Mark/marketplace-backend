package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.CategoriaRequest;
import com.marketplace.marketplace_backend.dto.CategoriaResponse;
import com.marketplace.marketplace_backend.model.Categoria;
import com.marketplace.marketplace_backend.service.CategoriaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaResponse> listarCategorias() {
        return categoriaService.listarCategorias()
                .stream()
                .map(c -> new CategoriaResponse(c.getId(), c.getNome(), c.getDescricao()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoriaResponse buscarPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.buscarPorId(id);
        return new CategoriaResponse(categoria.getId(), categoria.getNome(), categoria.getDescricao());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CategoriaResponse criarCategoria(@RequestBody CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.getNome());
        categoria.setDescricao(request.getDescricao());

        Categoria salva = categoriaService.criarCategoria(categoria);
        return new CategoriaResponse(salva.getId(), salva.getNome(), salva.getDescricao());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CategoriaResponse atualizarCategoria(@PathVariable Long id, @RequestBody CategoriaRequest request) {
        Categoria categoriaAtualizada = new Categoria();
        categoriaAtualizada.setNome(request.getNome());
        categoriaAtualizada.setDescricao(request.getDescricao());

        Categoria categoria = categoriaService.atualizarCategoria(id, categoriaAtualizada);
        return new CategoriaResponse(categoria.getId(), categoria.getNome(), categoria.getDescricao());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deletarCategoria(@PathVariable Long id) {
        categoriaService.deletarCategoria(id);
    }
}
