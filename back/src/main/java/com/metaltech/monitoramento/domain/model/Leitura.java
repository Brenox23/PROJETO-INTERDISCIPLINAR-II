package com.metaltech.monitoramento.domain.model;

import com.metaltech.monitoramento.domain.Avaliavel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * ABSTRAÇÃO: não existe "leitura genérica", apenas leitura de peça ou de nível.
 * HERANÇA (JOINED): tabela "leitura" com os campos comuns e uma tabela por subclasse.
 * POLIMORFISMO: cada subclasse implementa avaliarAlerta() e descricao() do seu jeito.
 */
@Entity
@Table(name = "leitura", indexes = {
        @Index(name = "idx_leitura_esteira_data", columnList = "esteira_id, registrado_em")
})
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Leitura extends EntidadeBase implements Avaliavel {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "esteira_id", nullable = false)
    private Esteira esteira;

    /** Momento em que o sensor registrou a leitura. */
    @Column(name = "registrado_em", nullable = false, updatable = false)
    private LocalDateTime registradoEm;

    /** Construtor exigido pelo JPA. */
    protected Leitura() {
    }

    protected Leitura(Esteira esteira, LocalDateTime registradoEm) {
        if (esteira == null) {
            throw new IllegalArgumentException("A leitura precisa estar vinculada a uma esteira.");
        }
        this.esteira = esteira;
        this.registradoEm = (registradoEm != null) ? registradoEm : LocalDateTime.now();
    }

    /**
     * Texto legível da leitura, usado nas mensagens de alerta.
     */
    public abstract String descricao();

    public Esteira getEsteira() {
        return esteira;
    }

    public LocalDateTime getRegistradoEm() {
        return registradoEm;
    }
}