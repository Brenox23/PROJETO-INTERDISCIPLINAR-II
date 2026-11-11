package com.metaltech.monitoramento.dto.response;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import com.metaltech.monitoramento.domain.model.Esteira;
import com.metaltech.monitoramento.domain.model.LeituraNivel;

import java.time.LocalDateTime;

/**
 * Nível atual do reservatório, com o alerta já avaliado.
 * É o principal dado exibido na RA (indicador visual de alerta).
 */
public record NivelAtualResponse(
        Long esteiraId,
        String esteiraNome,
        double distanciaCm,
        double percentual,
        NivelAlerta nivelAlerta,
        double limiteAtencaoPercentual,
        double limiteCriticoPercentual,
        LocalDateTime registradoEm
) {

    public static NivelAtualResponse de(LeituraNivel leitura) {
        Esteira esteira = leitura.getEsteira();
        return new NivelAtualResponse(
                esteira.getId(),
                esteira.getNome(),
                leitura.getDistanciaCm(),
                leitura.getPercentual(),
                leitura.avaliarAlerta(),
                esteira.getLimiteAtencaoPercentual(),
                esteira.getLimiteCriticoPercentual(),
                leitura.getRegistradoEm()
        );
    }
}