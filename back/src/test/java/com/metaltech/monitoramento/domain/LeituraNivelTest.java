package com.metaltech.monitoramento.domain;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import com.metaltech.monitoramento.domain.model.Esteira;
import com.metaltech.monitoramento.domain.model.LeituraNivel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testa o cálculo do percentual do reservatório e a avaliação polimórfica do alerta.
 * Esteira de teste: altura 30 cm, atenção em 70%, crítico em 90%.
 */
class LeituraNivelTest {

    private Esteira esteira;

    @BeforeEach
    void setUp() {
        esteira = new Esteira("Esteira Teste", "Linha 1", 30.0, 70.0, 90.0);
    }

    @Test
    @DisplayName("Reservatório vazio (distância = altura) deve ser 0% e NORMAL")
    void reservatorioVazio() {
        LeituraNivel leitura = new LeituraNivel(esteira, null, 30.0);

        assertEquals(0.0, leitura.getPercentual(), 0.001);
        assertEquals(NivelAlerta.NORMAL, leitura.avaliarAlerta());
    }

    @Test
    @DisplayName("Distância de 9 cm deve dar 70% e nível ATENCAO")
    void nivelAtencao() {
        LeituraNivel leitura = new LeituraNivel(esteira, null, 9.0);

        assertEquals(70.0, leitura.getPercentual(), 0.001);
        assertEquals(NivelAlerta.ATENCAO, leitura.avaliarAlerta());
    }

    @Test
    @DisplayName("Distância de 2 cm deve dar 93,33% e nível CRITICO")
    void nivelCritico() {
        LeituraNivel leitura = new LeituraNivel(esteira, null, 2.0);

        assertEquals(93.33, leitura.getPercentual(), 0.001);
        assertEquals(NivelAlerta.CRITICO, leitura.avaliarAlerta());
    }

    @Test
    @DisplayName("Reservatório totalmente cheio (distância 0) deve ser 100%")
    void reservatorioCheio() {
        LeituraNivel leitura = new LeituraNivel(esteira, null, 0.0);

        assertEquals(100.0, leitura.getPercentual(), 0.001);
    }

    @Test
    @DisplayName("Distância maior que a altura (ruído do sensor) deve ser limitada a 0%")
    void distanciaMaiorQueAltura() {
        LeituraNivel leitura = new LeituraNivel(esteira, null, 45.0);

        assertEquals(0.0, leitura.getPercentual(), 0.001);
    }

    @Test
    @DisplayName("Distância negativa deve ser recusada")
    void distanciaNegativa() {
        assertThrows(IllegalArgumentException.class, () -> new LeituraNivel(esteira, null, -1.0));
    }

    @Test
    @DisplayName("Esteira com limite de atenção maior que o crítico deve ser recusada")
    void limitesInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Esteira("Esteira Inválida", "Linha X", 30.0, 95.0, 90.0));
    }

    @Test
    @DisplayName("A descrição da leitura deve mencionar o reservatório")
    void descricao() {
        LeituraNivel leitura = new LeituraNivel(esteira, null, 2.0);

        assertTrue(leitura.descricao().startsWith("Reservatório em"));
    }
}