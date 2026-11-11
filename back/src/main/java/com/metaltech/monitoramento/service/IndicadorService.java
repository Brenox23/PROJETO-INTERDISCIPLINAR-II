package com.metaltech.monitoramento.service;

import com.metaltech.monitoramento.domain.enums.TipoPeca;
import com.metaltech.monitoramento.domain.model.Esteira;
import com.metaltech.monitoramento.dto.response.ContagemResponse;
import com.metaltech.monitoramento.dto.response.NivelAtualResponse;
import com.metaltech.monitoramento.exception.RecursoNaoEncontradoException;
import com.metaltech.monitoramento.repository.LeituraNivelRepository;
import com.metaltech.monitoramento.repository.LeituraPecaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Indicadores em tempo (quase) real, consumidos pelo dashboard e pela RA.
 */
@Service
public class IndicadorService {

    private final LeituraPecaRepository leituraPecaRepository;
    private final LeituraNivelRepository leituraNivelRepository;
    private final EsteiraService esteiraService;

    public IndicadorService(LeituraPecaRepository leituraPecaRepository,
                            LeituraNivelRepository leituraNivelRepository,
                            EsteiraService esteiraService) {
        this.leituraPecaRepository = leituraPecaRepository;
        this.leituraNivelRepository = leituraNivelRepository;
        this.esteiraService = esteiraService;
    }

    /**
     * Contagem de peças por tipo.
     * Sem período: contagem total. Com período: apenas dentro do intervalo.
     */
    @Transactional(readOnly = true)
    public ContagemResponse contagem(Long esteiraId, LocalDateTime inicio, LocalDateTime fim) {
        Esteira esteira = esteiraService.buscarEntidade(esteiraId);

        boolean comPeriodo = (inicio != null || fim != null);
        if (comPeriodo) {
            validarPeriodo(inicio, fim);
        }

        long metalicas = contar(esteiraId, TipoPeca.METALICA, inicio, fim, comPeriodo);
        long naoMetalicas = contar(esteiraId, TipoPeca.NAO_METALICA, inicio, fim, comPeriodo);
        long indefinidas = contar(esteiraId, TipoPeca.INDEFINIDA, inicio, fim, comPeriodo);

        return ContagemResponse.de(esteira.getId(), esteira.getNome(),
                metalicas, naoMetalicas, indefinidas);
    }

    /**
     * Última leitura do reservatório, já com o nível de alerta avaliado.
     */
    @Transactional(readOnly = true)
    public NivelAtualResponse nivelAtual(Long esteiraId) {
        esteiraService.buscarEntidade(esteiraId);
        return leituraNivelRepository.findFirstByEsteiraIdOrderByRegistradoEmDesc(esteiraId)
                .map(NivelAtualResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma leitura de nível registrada para a esteira " + esteiraId + "."));
    }

    private long contar(Long esteiraId, TipoPeca tipo, LocalDateTime inicio,
                        LocalDateTime fim, boolean comPeriodo) {
        return comPeriodo
                ? leituraPecaRepository.countByEsteiraIdAndTipoAndRegistradoEmBetween(esteiraId, tipo, inicio, fim)
                : leituraPecaRepository.countByEsteiraIdAndTipo(esteiraId, tipo);
    }

    private void validarPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Para filtrar por período, informe início e fim.");
        }
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("O início do período deve ser anterior ao fim.");
        }
    }
}