package com.metaltech.monitoramento.domain.enums;

/**
 * Níveis de alerta usados pela esteira, pelo dashboard e pela RA.
 */
public enum NivelAlerta {

    NORMAL("Normal"),
    ATENCAO("Atenção"),
    CRITICO("Crítico");

    private final String descricao;

    NivelAlerta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Indica se o nível exige que um alerta seja registrado.
     */
    public boolean exigeAtencao() {
        return this != NORMAL;
    }
}