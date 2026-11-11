package com.metaltech.monitoramento.classificacao;

import com.metaltech.monitoramento.domain.enums.TipoPeca;
import org.springframework.stereotype.Component;

/**
 * Regra de classificação baseada nos sensores da esteira:
 * - Indutivo detecta apenas metal.
 * - Capacitivo detecta qualquer material (inclusive metal).
 *
 * Portanto:
 * indutivo ativo                  -> METALICA
 * só capacitivo ativo             -> NAO_METALICA
 * nenhum ativo                    -> INDEFINIDA (possível falha de sensor)
 */
@Component
public class ClassificadorIndutivoCapacitivo implements ClassificadorPeca {

    @Override
    public TipoPeca classificar(boolean sensorIndutivo, boolean sensorCapacitivo) {
        if (sensorIndutivo) {
            return TipoPeca.METALICA;
        }
        if (sensorCapacitivo) {
            return TipoPeca.NAO_METALICA;
        }
        return TipoPeca.INDEFINIDA;
    }
}