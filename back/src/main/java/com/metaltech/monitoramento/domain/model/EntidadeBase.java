package com.metaltech.monitoramento.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

/**
 * ABSTRAÇÃO + HERANÇA: superclasse de todas as entidades.
 * Centraliza o id e a data de criação, evitando repetição de código.
 */
@MappedSuperclass
public abstract class EntidadeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}