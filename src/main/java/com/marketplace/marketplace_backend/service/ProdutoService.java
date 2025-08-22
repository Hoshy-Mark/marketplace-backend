package com.marketplace.marketplace_backend.service;

import com.marketplace.marketplace_backend.model.Produto;
import com.marketplace.marketplace_backend.model.Role;
import com.marketplace.marketplace_backend.model.Usuario;
import com.marketplace.marketplace_backend.repository.ProdutoRepository;
import com.marketplace.marketplace_backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProdutoService(ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Criar novo produto (apenas VENDEDOR)
    public Produto createProduto(String nome, String descricao, BigDecimal preco, Integer quantidadeEstoque, Long categoriaId, Long vendedorId) {
        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new RuntimeException("Vendedor não encontrado"));

        if (vendedor.getRole() != Role.VENDEDOR) {
            throw new RuntimeException("Apenas vendedores podem criar produtos");
        }

        Produto produto = Produto.builder()
                .nome(nome)
                .descricao(descricao)
                .preco(preco)
                .quantidadeEstoque(quantidadeEstoque)
                .categoriaId(categoriaId)
                .vendedor(vendedor)
                .build();

        return produtoRepository.save(produto);
    }

    // Buscar por ID
    public Produto getProdutoById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
    }

    // Listar todos
    public List<Produto> getAllProdutos() {
        return produtoRepository.findAll();
    }

    // Atualizar produto (somente dono + vendedor)
    public Produto updateProduto(Long id, String nome, String descricao, BigDecimal preco, Integer quantidadeEstoque, Long categoriaId, Long vendedorId) {
        Produto produto = getProdutoById(id);

        if (!produto.getVendedor().getId().equals(vendedorId)) {
            throw new RuntimeException("Somente o vendedor dono do produto pode atualizá-lo");
        }

        if (nome != null) produto.setNome(nome);
        if (descricao != null) produto.setDescricao(descricao);
        if (preco != null) produto.setPreco(preco);
        if (quantidadeEstoque != null) produto.setQuantidadeEstoque(quantidadeEstoque);
        if (categoriaId != null) produto.setCategoriaId(categoriaId);

        return produtoRepository.save(produto);
    }

    // Deletar produto (somente dono + vendedor)
    public void deleteProduto(Long id, Long vendedorId) {
        Produto produto = getProdutoById(id);

        if (!produto.getVendedor().getId().equals(vendedorId)) {
            throw new RuntimeException("Somente o vendedor dono do produto pode deletá-lo");
        }

        produtoRepository.delete(produto);
    }
}