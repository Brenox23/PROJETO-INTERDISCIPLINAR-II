package com.metaltech.monitoramento.domain.model;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import com.metaltech.monitoramento.domain.enums.TipoPeca;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * HERANÇA: leitura gerada a cada peça que passa pela esteira.
 */
@Entity
@Table(name = "leitura_peca")
@PrimaryKeyJoinColumn(name = "leitura_id")
public class LeituraPeca extends Leitura {

    @Column(name = "sensor_indutivo", nullable = false)
    private boolean sensorIndutivo;

    @Column(name = "sensor_capacitivo", nullable = false)
    private boolean sensorCapacitivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPeca tipo;

    /** Construtor exigido pelo JPA. */
    protected LeituraPeca() {
    }

    public LeituraPeca(Esteira esteira, LocalDateTime registradoEm,
                       boolean sensorIndutivo, boolean sensorCapacitivo, TipoPeca tipo) {
        super(esteira, registradoEm);
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo da peça deve ser informado pelo classificador.");
        }
        this.sensorIndutivo = sensorIndutivo;
        this.sensorCapacitivo = sensorCapacitivo;
        this.tipo = tipo;
    }

    /**
     * POLIMORFISMO: uma peça só gera alerta quando nenhum sensor
     * conseguiu identificar o material (possível falha de sensor).
     */
    @Override
    public NivelAlerta avaliarAlerta() {
        return (tipo == TipoPeca.INDEFINIDA) ? NivelAlerta.ATENCAO : NivelAlerta.NORMAL;
    }

    @Override
    public String descricao() {
        if (tipo == TipoPeca.INDEFINIDA) {
            return "Peça não identificada pelos sensores de material";
        }
        return "Peça " + tipo.getDescricao().toLowerCase() + " detectada";
    }

    public boolean isMetalica() {
        return tipo == TipoPeca.METALICA;
    }

    public boolean isSensorIndutivo() {
        return sensorIndutivo;
    }

    public boolean isSensorCapacitivo() {
        return sensorCapacitivo;
    }

    public TipoPeca getTipo() {
        return tipo;
    }
}