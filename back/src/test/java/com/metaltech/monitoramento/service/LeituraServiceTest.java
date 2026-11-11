package com.metaltech.monitoramento.service;

import com.metaltech.monitoramento.classificacao.ClassificadorIndutivoCapacitivo;
import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import com.metaltech.monitoramento.domain.enums.TipoPeca;
import com.metaltech.monitoramento.domain.model.Esteira;
import com.metaltech.monitoramento.domain.model.LeituraNivel;
import com.metaltech.monitoramento.domain.model.LeituraPeca;
import com.metaltech.monitoramento.dto.request.LeituraNivelRequest;
import com.metaltech.monitoramento.dto.request.LeituraPecaRequest;
import com.metaltech.monitoramento.dto.response.LeituraResponse;
import com.metaltech.monitoramento.exception.LeituraInvalidaException;
import com.metaltech.monitoramento.repository.LeituraNivelRepository;
import com.metaltech.monitoramento.repository.LeituraPecaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testa o fluxo de recebimento de leituras: classificação, cálculo e validações.
 */
@ExtendWith(MockitoExtension.class)
class LeituraServiceTest {

    @Mock
    private LeituraPecaRepository leituraPecaRepository;

    @Mock
    private LeituraNivelRepository leituraNivelRepository;

    @Mock
    private EsteiraService esteiraService;

    @Mock
    private AlertaService alertaService;

    private LeituraService leituraService;
    private Esteira esteira;

    @BeforeEach
    void setUp() {
        // Classificador real: queremos testar a regra de verdade junto com o service.
        leituraService = new LeituraService(
                leituraPecaRepository,
                leituraNivelRepository,
                esteiraService,
                new ClassificadorIndutivoCapacitivo(),
                alertaService
        );
        esteira = new Esteira("Esteira Teste", "Linha 1", 30.0, 70.0, 90.0);
    }

    @Test
    @DisplayName("Deve classificar e salvar uma peça metálica, avaliando o alerta")
    void deveRegistrarPecaMetalica() {
        when(esteiraService.buscarEntidade(1L)).thenReturn(esteira);
        when(leituraPecaRepository.save(any(LeituraPeca.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        LeituraResponse resposta = leituraService.registrarPeca(new LeituraPecaRequest(1L, true, true, null));

        assertEquals("PECA", resposta.tipoLeitura());
        assertEquals(TipoPeca.METALICA, resposta.tipoPeca());
        verify(alertaService).avaliarEGerar(any(LeituraPeca.class));
    }

    @Test
    @DisplayName("Deve classificar como NÃO METÁLICA quando só o capacitivo detecta")
    void deveRegistrarPecaNaoMetalica() {
        when(esteiraService.buscarEntidade(1L)).thenReturn(esteira);
        when(leituraPecaRepository.save(any(LeituraPeca.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        LeituraResponse resposta = leituraService.registrarPeca(new LeituraPecaRequest(1L, false, true, null));

        assertEquals(TipoPeca.NAO_METALICA, resposta.tipoPeca());
    }

    @Test
    @DisplayName("Deve calcular o percentual e o alerta da leitura de nível")
    void deveRegistrarNivel() {
        when(esteiraService.buscarEntidade(1L)).thenReturn(esteira);
        when(leituraNivelRepository.save(any(LeituraNivel.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        LeituraResponse resposta = leituraService.registrarNivel(new LeituraNivelRequest(1L, 2.0, null));

        assertEquals("NIVEL", resposta.tipoLeitura());
        assertEquals(93.33, resposta.percentual(), 0.001);
        assertEquals(NivelAlerta.CRITICO, resposta.nivelAlerta());
        verify(alertaService).avaliarEGerar(any(LeituraNivel.class));
    }

    @Test
    @DisplayName("Esteira desativada não deve aceitar leituras")
    void esteiraDesativadaRecusaLeitura() {
        esteira.desativar();
        when(esteiraService.buscarEntidade(1L)).thenReturn(esteira);

        assertThrows(LeituraInvalidaException.class,
                () -> leituraService.registrarPeca(new LeituraPecaRequest(1L, true, true, null)));
        verify(leituraPecaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Histórico com início depois do fim deve ser recusado")
    void historicoComPeriodoInvalido() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 10, 18, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 11, 10, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> leituraService.historico(1L, inicio, fim));
    }
}