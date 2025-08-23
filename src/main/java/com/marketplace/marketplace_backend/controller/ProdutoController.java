package com.marketplace.marketplace_backend.controller;

import com.marketplace.marketplace_backend.dto.ProdutoRequest;
import com.marketplace.marketplace_backend.dto.ProdutoResponse;
import com.marketplace.marketplace_backend.model.Produto;
import com.marketplace.marketplace_backend.model.Usuario;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import com.marketplace.marketplace_backend.service.ProdutoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final UsuarioRepository usuarioRepository;

    public ProdutoController(ProdutoService produtoService, UsuarioRepository usuarioRepository) {
        this.produtoService = produtoService;
        this.usuarioRepository = usuarioRepository;
    }

    // Criar produto - apenas vendedores
    @PostMapping
    @PreAuthorize("hasRole('VENDEDOR')")
    public ResponseEntity<ProdutoResponse> createProduto(
            @RequestBody ProdutoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Usuario vendedor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Produto produto = produtoService.createProduto(
                request.getNome(),
                request.getDescricao(),
                request.getPreco(),
                request.getQuantidadeEstoque(),
                request.getCategoriaId(),
                vendedor.getId()
        );

        return ResponseEntity.ok(toResponse(produto));
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> getProdutoById(@PathVariable Long id) {
        Produto produto = produtoService.getProdutoById(id);
        return ResponseEntity.ok(toResponse(produto));
    }

    // Listar produtos com filtros e paginação
    @GetMapping
    public ResponseEntity<Page<ProdutoResponse>> listarProdutos(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ProdutoResponse> produtos = produtoService.listarProdutos(categoriaId, precoMax, pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(produtos);
    }

    // Atualizar produto - apenas vendedor dono
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VENDEDOR')")
    public ResponseEntity<ProdutoResponse> updateProduto(
            @PathVariable Long id,
            @RequestBody ProdutoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Usuario vendedor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Produto produto = produtoService.updateProduto(
                id,
                request.getNome(),
                request.getDescricao(),
                request.getPreco(),
                request.getQuantidadeEstoque(),
                request.getCategoriaId(),
                vendedor.getId()
        );

        return ResponseEntity.ok(toResponse(produto));
    }

    // Deletar produto - apenas vendedor dono
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('VENDEDOR')")
    public ResponseEntity<Void> deleteProduto(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Usuario vendedor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        produtoService.deleteProduto(id, vendedor.getId());

        return ResponseEntity.noContent().build();
    }

    private ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidadeEstoque(),
                produto.getCategoria().getId(),
                produto.getVendedor().getId(),
                produto.getDataCriacao().toString()
        );
    }
}
