package com.metaltech.monitoramento.domain.model;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Alerta gerado quando uma leitura sai do padrão esperado.
 */
@Entity
@Table(name = "alerta", indexes = {
        @Index(name = "idx_alerta_resolvido", columnList = "resolvido")
})
public class Alerta extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "esteira_id", nullable = false)
    private Esteira esteira;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leitura_id")
    private Leitura leitura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelAlerta nivel;

    @Column(nullable = false, length = 255)
    private String mensagem;

    @Column(nullable = false)
    private boolean resolvido = false;

    @Column(name = "resolvido_em")
    private LocalDateTime resolvidoEm;

    /** Construtor exigido pelo JPA. */
    protected Alerta() {
    }

    public Alerta(Esteira esteira, Leitura leitura, NivelAlerta nivel, String mensagem) {
        if (esteira == null) {
            throw new IllegalArgumentException("O alerta precisa estar vinculado a uma esteira.");
        }
        if (nivel == null || !nivel.exigeAtencao()) {
            throw new IllegalArgumentException("Um alerta deve ter nível ATENCAO ou CRITICO.");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("A mensagem do alerta é obrigatória.");
        }
        this.esteira = esteira;
        this.leitura = leitura;
        this.nivel = nivel;
        this.mensagem = mensagem.trim();
    }

    /**
     * POLIMORFISMO na prática: o método não sabe se recebeu uma LeituraPeca
     * ou uma LeituraNivel; cada uma responde avaliarAlerta() e descricao() à sua maneira.
     */
    public static Alerta gerarDe(Leitura leitura) {
        NivelAlerta nivel = leitura.avaliarAlerta();
        if (!nivel.exigeAtencao()) {
            throw new IllegalStateException("Leitura em nível normal não gera alerta.");
        }
        String mensagem = leitura.descricao() + " - nível " + nivel.getDescricao();
        return new Alerta(leitura.getEsteira(), leitura, nivel, mensagem);
    }

    public void resolver() {
        if (resolvido) {
            throw new IllegalStateException("Este alerta já foi resolvido.");
        }
        this.resolvido = true;
        this.resolvidoEm = LocalDateTime.now();
    }

    public Esteira getEsteira() {
        return esteira;
    }

    public Leitura getLeitura() {
        return leitura;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isResolvido() {
        return resolvido;
    }

    public LocalDateTime getResolvidoEm() {
        return resolvidoEm;
    }
}