package com.metaltech.monitoramento.domain.model;

import com.metaltech.monitoramento.domain.enums.Perfil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Usuário da plataforma. A senha NUNCA é guardada em texto puro:
 * a classe só recebe o hash (gerado com BCrypt no service).
 */
@Entity
@Table(name = "usuario")
public class Usuario extends EntidadeBase {

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil;

    @Column(nullable = false)
    private boolean ativo = true;

    /** Construtor exigido pelo JPA. */
    protected Usuario() {
    }

    public Usuario(String nome, String email, String senhaHash, Perfil perfil) {
        atualizarDados(nome, email);
        alterarSenha(senhaHash);
        alterarPerfil(perfil);
    }

    public void atualizarDados(String nome, String email) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do usuário é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail do usuário é obrigatório.");
        }
        this.nome = nome.trim();
        this.email = email.trim().toLowerCase();
    }

    public void alterarSenha(String novoSenhaHash) {
        if (novoSenhaHash == null || novoSenhaHash.isBlank()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        this.senhaHash = novoSenhaHash;
    }

    public void alterarPerfil(Perfil perfil) {
        if (perfil == null) {
            throw new IllegalArgumentException("O perfil do usuário é obrigatório.");
        }
        this.perfil = perfil;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }
}