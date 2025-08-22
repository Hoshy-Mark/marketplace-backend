package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.ProdutoRequest;
import com.marketplace.marketplace_backend.dto.ProdutoResponse;
import com.marketplace.marketplace_backend.model.Produto;
import com.marketplace.marketplace_backend.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    //Criar Produto
    @PreAuthorize("hasRole('VENDEDOR')")
    @PostMapping
    public ResponseEntity<?> createProduto(@Valid @RequestBody ProdutoRequest request) {
        try {
            Produto produto = produtoService.createProduto(
                    request.getNome(),
                    request.getDescricao(),
                    request.getPreco(),
                    request.getQuantidadeEstoque(),
                    request.getCategoriaId(),
                    request.getVendedorId()
            );

            ProdutoResponse response = new ProdutoResponse(
                    produto.getId(),
                    produto.getNome(),
                    produto.getDescricao(),
                    produto.getPreco(),
                    produto.getQuantidadeEstoque(),
                    produto.getCategoriaId(),
                    produto.getVendedor().getId(),
                    produto.getDataCriacao()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //Mostrar Produto
    @GetMapping("/{id}")
    public ResponseEntity<?> getProdutoById(@PathVariable Long id) {
        try {
            Produto produto = produtoService.getProdutoById(id);
            ProdutoResponse response = new ProdutoResponse(
                    produto.getId(),
                    produto.getNome(),
                    produto.getDescricao(),
                    produto.getPreco(),
                    produto.getQuantidadeEstoque(),
                    produto.getCategoriaId(),
                    produto.getVendedor().getId(),
                    produto.getDataCriacao()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //Listar produtos
    @GetMapping
    public List<ProdutoResponse> getAllProdutos() {
        return produtoService.getAllProdutos().stream()
                .map(p -> new ProdutoResponse(
                        p.getId(),
                        p.getNome(),
                        p.getDescricao(),
                        p.getPreco(),
                        p.getQuantidadeEstoque(),
                        p.getCategoriaId(),
                        p.getVendedor().getId(),
                        p.getDataCriacao()
                ))
                .collect(Collectors.toList());
    }

    //Atualizar produto
    @PreAuthorize("hasRole('VENDEDOR')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduto(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
        try {
            Produto produto = produtoService.updateProduto(
                    id,
                    request.getNome(),
                    request.getDescricao(),
                    request.getPreco(),
                    request.getQuantidadeEstoque(),
                    request.getCategoriaId(),
                    request.getVendedorId()
            );

            ProdutoResponse response = new ProdutoResponse(
                    produto.getId(),
                    produto.getNome(),
                    produto.getDescricao(),
                    produto.getPreco(),
                    produto.getQuantidadeEstoque(),
                    produto.getCategoriaId(),
                    produto.getVendedor().getId(),
                    produto.getDataCriacao()
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    //Deletar Produto
    @PreAuthorize("hasRole('VENDEDOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduto(@PathVariable Long id, @RequestParam Long vendedorId) {
        try {
            produtoService.deleteProduto(id, vendedorId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
