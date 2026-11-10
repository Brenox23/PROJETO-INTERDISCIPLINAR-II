package com.metaltech.monitoramento.domain.enums;

/**
 * Classificação da peça a partir dos sensores indutivo e capacitivo.
 */
public enum TipoPeca {

    METALICA("Metálica"),
    NAO_METALICA("Não metálica"),
    INDEFINIDA("Indefinida");

    private final String descricao;

    TipoPeca(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}