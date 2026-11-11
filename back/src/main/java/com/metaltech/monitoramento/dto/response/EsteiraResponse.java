package com.metaltech.monitoramento.dto.response;

import com.metaltech.monitoramento.domain.model.Esteira;

import java.time.LocalDateTime;

public record EsteiraResponse(
        Long id,
        String nome,
        String localizacao,
        double alturaReservatorioCm,
        double limiteAtencaoPercentual,
        double limiteCriticoPercentual,
        boolean ativa,
        LocalDateTime criadoEm
) {

    public static EsteiraResponse de(Esteira esteira) {
        return new EsteiraResponse(
                esteira.getId(),
                esteira.getNome(),
                esteira.getLocalizacao(),
                esteira.getAlturaReservatorioCm(),
                esteira.getLimiteAtencaoPercentual(),
                esteira.getLimiteCriticoPercentual(),
                esteira.isAtiva(),
                esteira.getCriadoEm()
        );
    }
}