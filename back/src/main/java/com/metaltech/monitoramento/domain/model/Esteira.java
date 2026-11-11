package com.metaltech.monitoramento.domain.model;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Esteira de triagem monitorada pela plataforma.
 * ENCAPSULAMENTO: atributos privados, alterados apenas por métodos
 * que validam as regras de negócio.
 */
@Entity
@Table(name = "esteira")
public class Esteira extends EntidadeBase {

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(length = 150)
    private String localizacao;

    /** Distância (cm) entre o sensor de distância e o fundo do reservatório vazio. */
    @Column(name = "altura_reservatorio_cm", nullable = false)
    private double alturaReservatorioCm;

    @Column(name = "limite_atencao_percentual", nullable = false)
    private double limiteAtencaoPercentual;

    @Column(name = "limite_critico_percentual", nullable = false)
    private double limiteCriticoPercentual;

    @Column(nullable = false)
    private boolean ativa = true;

    /** Construtor exigido pelo JPA. */
    protected Esteira() {
    }

    public Esteira(String nome, String localizacao, double alturaReservatorioCm,
                   double limiteAtencaoPercentual, double limiteCriticoPercentual) {
        atualizarDados(nome, localizacao, alturaReservatorioCm,
                limiteAtencaoPercentual, limiteCriticoPercentual);
    }

    public void atualizarDados(String nome, String localizacao, double alturaReservatorioCm,
                               double limiteAtencaoPercentual, double limiteCriticoPercentual) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da esteira é obrigatório.");
        }
        if (alturaReservatorioCm <= 0) {
            throw new IllegalArgumentException("A altura do reservatório deve ser maior que zero.");
        }
        if (limiteAtencaoPercentual <= 0
                || limiteCriticoPercentual > 100
                || limiteAtencaoPercentual >= limiteCriticoPercentual) {
            throw new IllegalArgumentException(
                    "Os limites devem respeitar: 0 < atenção < crítico <= 100.");
        }
        this.nome = nome.trim();
        this.localizacao = (localizacao == null) ? null : localizacao.trim();
        this.alturaReservatorioCm = alturaReservatorioCm;
        this.limiteAtencaoPercentual = limiteAtencaoPercentual;
        this.limiteCriticoPercentual = limiteCriticoPercentual;
    }

    /**
     * Converte a distância medida pelo sensor em percentual de ocupação do reservatório.
     * Quanto MENOR a distância, MAIS cheio o reservatório.
     */
    public double calcularPercentual(double distanciaCm) {
        if (distanciaCm < 0) {
            throw new IllegalArgumentException("A distância medida não pode ser negativa.");
        }
        double ocupado = alturaReservatorioCm - distanciaCm;
        double percentual = (ocupado / alturaReservatorioCm) * 100.0;
        double limitado = Math.max(0.0, Math.min(100.0, percentual));
        return Math.round(limitado * 100.0) / 100.0;
    }

    /**
     * Classifica um percentual de ocupação de acordo com os limites desta esteira.
     */
    public NivelAlerta avaliarNivel(double percentual) {
        if (percentual >= limiteCriticoPercentual) {
            return NivelAlerta.CRITICO;
        }
        if (percentual >= limiteAtencaoPercentual) {
            return NivelAlerta.ATENCAO;
        }
        return NivelAlerta.NORMAL;
    }

    public void ativar() {
        this.ativa = true;
    }

    public void desativar() {
        this.ativa = false;
    }

    public String getNome() {
        return nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public double getAlturaReservatorioCm() {
        return alturaReservatorioCm;
    }

    public double getLimiteAtencaoPercentual() {
        return limiteAtencaoPercentual;
    }

    public double getLimiteCriticoPercentual() {
        return limiteCriticoPercentual;
    }

    public boolean isAtiva() {
        return ativa;
    }
}