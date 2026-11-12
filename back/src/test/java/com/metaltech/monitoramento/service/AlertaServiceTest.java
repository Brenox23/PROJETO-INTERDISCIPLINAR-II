package com.metaltech.monitoramento.service;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import com.metaltech.monitoramento.domain.enums.TipoPeca;
import com.metaltech.monitoramento.domain.model.Alerta;
import com.metaltech.monitoramento.domain.model.Esteira;
import com.metaltech.monitoramento.domain.model.LeituraNivel;
import com.metaltech.monitoramento.domain.model.LeituraPeca;
import com.metaltech.monitoramento.exception.RecursoNaoEncontradoException;
import com.metaltech.monitoramento.repository.AlertaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testa a geração de alertas usando um repositório "dublê" (Mockito),
 * sem precisar de banco de dados.
 */
@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @InjectMocks
    private AlertaService alertaService;

    private Esteira esteira;

    @BeforeEach
    void setUp() {
        esteira = new Esteira("Esteira Teste", "Linha 1", 30.0, 70.0, 90.0);
    }

    @Test
    @DisplayName("Leitura em nível normal não deve gerar alerta")
    void leituraNormalNaoGeraAlerta() {
        LeituraNivel leitura = new LeituraNivel(esteira, null, 25.0); // ~16,7%

        Optional<Alerta> resultado = alertaService.avaliarEGerar(leitura);

        assertTrue(resultado.isEmpty());
        verify(alertaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Nível crítico deve gerar alerta CRITICO quando não há outro aberto")
    void nivelCriticoGeraAlerta() {
        LeituraNivel leitura = new LeituraNivel(esteira, null, 2.0); // 93,33%
        when(alertaRepository.existeAlertaDeNivelAberto(any(), eq(NivelAlerta.CRITICO))).thenReturn(false);
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Optional<Alerta> resultado = alertaService.avaliarEGerar(leitura);

        assertTrue(resultado.isPresent());
        assertEquals(NivelAlerta.CRITICO, resultado.get().getNivel());
        assertTrue(resultado.get().getMensagem().startsWith("Reservatório em"));
    }

    @Test
    @DisplayName("Não deve duplicar alerta de nível que já está aberto")
    void naoDuplicaAlertaDeNivel() {
        LeituraNivel leitura = new LeituraNivel(esteira, null, 2.0);
        when(alertaRepository.existeAlertaDeNivelAberto(any(), eq(NivelAlerta.CRITICO))).thenReturn(true);

        Optional<Alerta> resultado = alertaService.avaliarEGerar(leitura);

        assertTrue(resultado.isEmpty());
        verify(alertaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Peça não identificada pelos sensores deve gerar alerta de ATENCAO")
    void pecaIndefinidaGeraAtencao() {
        LeituraPeca leitura = new LeituraPeca(esteira, null, false, false, TipoPeca.INDEFINIDA);
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Optional<Alerta> resultado = alertaService.avaliarEGerar(leitura);

        assertTrue(resultado.isPresent());
        assertEquals(NivelAlerta.ATENCAO, resultado.get().getNivel());
    }

    @Test
    @DisplayName("Resolver alerta inexistente deve lançar RecursoNaoEncontradoException")
    void resolverAlertaInexistente() {
        when(alertaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> alertaService.resolver(99L));
    }

    @Test
    @DisplayName("Um alerta não pode ser resolvido duas vezes")
    void naoResolveDuasVezes() {
        Alerta alerta = new Alerta(esteira, null, NivelAlerta.CRITICO, "Alerta de teste");
        when(alertaRepository.findById(1L)).thenReturn(Optional.of(alerta));

        alertaService.resolver(1L);

        assertTrue(alerta.isResolvido());
        assertThrows(IllegalStateException.class, () -> alertaService.resolver(1L));
    }

    @Test
    @DisplayName("Nível voltando ao normal deve resolver automaticamente os alertas de nível abertos")
    void nivelNormalResolveAlertasAbertos() {
        Alerta alertaAberto = new Alerta(esteira, null, NivelAlerta.CRITICO, "Reservatório cheio");
        when(alertaRepository.buscarAlertasDeNivelAbertos(any())).thenReturn(java.util.List.of(alertaAberto));

        LeituraNivel leituraNormal = new LeituraNivel(esteira, null, 28.0); // ~6,7%
        Optional<Alerta> resultado = alertaService.avaliarEGerar(leituraNormal);

        assertTrue(resultado.isEmpty());
        assertTrue(alertaAberto.isResolvido());
    }
}