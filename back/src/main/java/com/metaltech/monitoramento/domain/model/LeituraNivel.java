package com.metaltech.monitoramento.domain.model;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * HERANÇA: leitura do sensor de distância sobre o reservatório de saída.
 */
@Entity
@Table(name = "leitura_nivel")
@PrimaryKeyJoinColumn(name = "leitura_id")
public class LeituraNivel extends Leitura {

    @Column(name = "distancia_cm", nullable = false)
    private double distanciaCm;

    /** Percentual de ocupação calculado no momento da leitura (fica salvo no histórico). */
    @Column(nullable = false)
    private double percentual;

    /** Construtor exigido pelo JPA. */
    protected LeituraNivel() {
    }

    public LeituraNivel(Esteira esteira, LocalDateTime registradoEm, double distanciaCm) {
        super(esteira, registradoEm);
        this.distanciaCm = distanciaCm;
        this.percentual = esteira.calcularPercentual(distanciaCm);
    }

    /**
     * POLIMORFISMO: o nível gera alerta comparando o percentual
     * com os limites configurados na esteira.
     */
    @Override
    public NivelAlerta avaliarAlerta() {
        return getEsteira().avaliarNivel(percentual);
    }

    @Override
    public String descricao() {
        return String.format("Reservatório em %.1f%%", percentual);
    }

    public double getDistanciaCm() {
        return distanciaCm;
    }

    public double getPercentual() {
        return percentual;
    }
}