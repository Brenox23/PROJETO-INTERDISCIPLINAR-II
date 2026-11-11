package com.metaltech.monitoramento.dto.response;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import com.metaltech.monitoramento.domain.model.Alerta;

import java.time.LocalDateTime;

public record AlertaResponse(
        Long id,
        Long esteiraId,
        NivelAlerta nivel,
        String mensagem,
        boolean resolvido,
        LocalDateTime criadoEm,
        LocalDateTime resolvidoEm
) {

    public static AlertaResponse de(Alerta alerta) {
        return new AlertaResponse(
                alerta.getId(),
                alerta.getEsteira().getId(),
                alerta.getNivel(),
                alerta.getMensagem(),
                alerta.isResolvido(),
                alerta.getCriadoEm(),
                alerta.getResolvidoEm()
        );
    }
}