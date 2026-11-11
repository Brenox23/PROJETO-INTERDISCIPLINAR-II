package com.metaltech.monitoramento.dto.response;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import com.metaltech.monitoramento.domain.enums.TipoPeca;
import com.metaltech.monitoramento.domain.model.Leitura;
import com.metaltech.monitoramento.domain.model.LeituraNivel;
import com.metaltech.monitoramento.domain.model.LeituraPeca;

import java.time.LocalDateTime;

/**
 * Representação única para os dois tipos de leitura.
 * Campos que não se aplicam ao tipo ficam nulos
 * (ex.: leitura de nível não tem tipoPeca).
 */
public record LeituraResponse(
        Long id,
        Long esteiraId,
        String tipoLeitura,
        LocalDateTime registradoEm,
        TipoPeca tipoPeca,
        Boolean sensorIndutivo,
        Boolean sensorCapacitivo,
        Double distanciaCm,
        Double percentual,
        NivelAlerta nivelAlerta
) {

    /**
     * POLIMORFISMO: recebe qualquer Leitura e monta a resposta
     * conforme o tipo real do objeto (pattern matching do Java 21).
     */
    public static LeituraResponse de(Leitura leitura) {
        if (leitura instanceof LeituraPeca peca) {
            return new LeituraResponse(
                    peca.getId(),
                    peca.getEsteira().getId(),
                    "PECA",
                    peca.getRegistradoEm(),
                    peca.getTipo(),
                    peca.isSensorIndutivo(),
                    peca.isSensorCapacitivo(),
                    null,
                    null,
                    peca.avaliarAlerta()
            );
        }
        if (leitura instanceof LeituraNivel nivel) {
            return new LeituraResponse(
                    nivel.getId(),
                    nivel.getEsteira().getId(),
                    "NIVEL",
                    nivel.getRegistradoEm(),
                    null,
                    null,
                    null,
                    nivel.getDistanciaCm(),
                    nivel.getPercentual(),
                    nivel.avaliarAlerta()
            );
        }
        throw new IllegalArgumentException("Tipo de leitura não suportado.");
    }
}