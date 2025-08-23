package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.model.Categoria;
import com.marketplace.marketplace_backend.model.Produto;
import com.marketplace.marketplace_backend.model.Role;
import com.marketplace.marketplace_backend.model.Usuario;
import com.marketplace.marketplace_backend.repository.CategoriaRepository;
import com.marketplace.marketplace_backend.repository.ProdutoRepository;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    private ProdutoRepository produtoRepository;
    private UsuarioRepository usuarioRepository;
    private CategoriaRepository categoriaRepository;
    private ProdutoService produtoService;

    private Usuario vendedor;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        produtoRepository = mock(ProdutoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        categoriaRepository = mock(CategoriaRepository.class);
        produtoService = new ProdutoService(produtoRepository, usuarioRepository, categoriaRepository);

        vendedor = new Usuario();
        vendedor.setId(1L);
        vendedor.setRole(Role.VENDEDOR);

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Eletrônicos");
    }

    @Test
    void testCreateProduto_Success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(vendedor));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Produto produto = produtoService.createProduto("Notebook", "Descrição", BigDecimal.valueOf(5000), 10, 1L, 1L);

        assertEquals("Notebook", produto.getNome());
        assertEquals(vendedor, produto.getVendedor());
        assertEquals(categoria, produto.getCategoria());
    }

    @Test
    void testCreateProduto_NotVendedor() {
        vendedor.setRole(Role.COMPRADOR);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(vendedor));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                produtoService.createProduto("Notebook", "Descrição", BigDecimal.valueOf(5000), 10, 1L, 1L)
        );
        assertEquals("Apenas vendedores podem criar produtos", ex.getMessage());
    }

    @Test
    void testGetProdutoById_Success() {
        Produto p = new Produto();
        p.setId(1L);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(p));

        Produto found = produtoService.getProdutoById(1L);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetProdutoById_NotFound() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> produtoService.getProdutoById(1L));
        assertEquals("Produto não encontrado com id: 1", ex.getMessage());
    }

    @Test
    void testUpdateProduto_Success() {
        Produto p = new Produto();
        p.setId(1L);
        p.setVendedor(vendedor);
        p.setCategoria(categoria);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(p));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Produto updated = produtoService.updateProduto(1L, "Novo Nome", null, BigDecimal.valueOf(5500), null, 1L, 1L);
        assertEquals("Novo Nome", updated.getNome());
        assertEquals(BigDecimal.valueOf(5500), updated.getPreco());
    }

    @Test
    void testUpdateProduto_NotOwner() {
        Produto p = new Produto();
        Usuario dono = new Usuario();
        dono.setId(2L);
        p.setVendedor(dono);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(p));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                produtoService.updateProduto(1L, "Nome", null, BigDecimal.valueOf(100), null, 2L, 1L)
        );
        assertEquals("Somente o vendedor dono do produto pode atualizá-lo", ex.getMessage());
    }

    @Test
    void testDeleteProduto_Success() {
        Produto p = new Produto();
        p.setId(1L);
        p.setVendedor(vendedor);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(p));

        produtoService.deleteProduto(1L, 1L);
        verify(produtoRepository, times(1)).delete(p);
    }

    @Test
    void testDeleteProduto_NotOwner() {
        Produto p = new Produto();
        Usuario dono = new Usuario();
        dono.setId(2L);
        p.setVendedor(dono);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(p));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> produtoService.deleteProduto(1L, 1L));
        assertEquals("Somente o vendedor dono do produto pode deletá-lo", ex.getMessage());
    }

    @Test
    void testListarProdutos_Filtros() {
        Produto p = new Produto();
        p.setId(1L);
        Page<Produto> page = new PageImpl<>(List.of(p));
        when(produtoRepository.findByCategoria_IdAndPrecoLessThanEqual(anyLong(), any(), any(Pageable.class))).thenReturn(page);

        Page<Produto> result = produtoService.listarProdutos(1L, BigDecimal.valueOf(5000), Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }
}
