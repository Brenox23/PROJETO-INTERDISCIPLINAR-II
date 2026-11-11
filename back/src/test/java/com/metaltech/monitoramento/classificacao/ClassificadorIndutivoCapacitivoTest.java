package com.metaltech.monitoramento.classificacao;

import com.metaltech.monitoramento.domain.enums.TipoPeca;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testa a regra de classificação com todas as combinações possíveis dos sensores.
 */
class ClassificadorIndutivoCapacitivoTest {

    private final ClassificadorPeca classificador = new ClassificadorIndutivoCapacitivo();

    @ParameterizedTest(name = "indutivo={0}, capacitivo={1} -> {2}")
    @DisplayName("Deve classificar a peça conforme a combinação dos sensores")
    @CsvSource({
            "true,  true,  METALICA",
            "true,  false, METALICA",
            "false, true,  NAO_METALICA",
            "false, false, INDEFINIDA"
    })
    void deveClassificarConformeSensores(boolean indutivo, boolean capacitivo, TipoPeca esperado) {
        assertEquals(esperado, classificador.classificar(indutivo, capacitivo));
    }
}