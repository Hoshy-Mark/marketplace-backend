package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.model.Categoria;
import com.marketplace.marketplace_backend.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {

    private CategoriaRepository categoriaRepository;
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        categoriaRepository = mock(CategoriaRepository.class);
        categoriaService = new CategoriaService(categoriaRepository);
    }

    @Test
    void testCriarCategoria() {
        Categoria c = new Categoria();
        c.setNome("Eletrônicos");
        when(categoriaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Categoria created = categoriaService.criarCategoria(c);
        assertEquals("Eletrônicos", created.getNome());
        verify(categoriaRepository, times(1)).save(c);
    }

    @Test
    void testAtualizarCategoria_Success() {
        Categoria c = new Categoria();
        c.setId(1L);
        c.setNome("Old");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(c));
        when(categoriaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Categoria updated = new Categoria();
        updated.setNome("Novo");
        updated.setDescricao("Descricao nova");

        Categoria result = categoriaService.atualizarCategoria(1L, updated);
        assertEquals("Novo", result.getNome());
        assertEquals("Descricao nova", result.getDescricao());
    }

    @Test
    void testAtualizarCategoria_NotFound() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> categoriaService.atualizarCategoria(1L, new Categoria()));
        assertEquals("Categoria não encontrada", ex.getMessage());
    }

    @Test
    void testDeletarCategoria_Success() {
        Categoria c = new Categoria();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(c));

        categoriaService.deletarCategoria(1L);
        verify(categoriaRepository, times(1)).delete(c);
    }

    @Test
    void testDeletarCategoria_NotFound() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> categoriaService.deletarCategoria(1L));
        assertEquals("Categoria não encontrada", ex.getMessage());
    }

    @Test
    void testListarCategorias() {
        Categoria c = new Categoria();
        when(categoriaRepository.findAll()).thenReturn(List.of(c));
        List<Categoria> result = categoriaService.listarCategorias();
        assertEquals(1, result.size());
    }

    @Test
    void testBuscarPorId_Success() {
        Categoria c = new Categoria();
        c.setId(1L);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(c));
        Categoria result = categoriaService.buscarPorId(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testBuscarPorId_NotFound() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> categoriaService.buscarPorId(1L));
        assertEquals("Categoria não encontrada", ex.getMessage());
    }
}
