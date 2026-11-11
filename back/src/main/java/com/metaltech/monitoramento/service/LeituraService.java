package com.metaltech.monitoramento.service;

import com.metaltech.monitoramento.classificacao.ClassificadorPeca;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Recebe, valida, classifica e armazena as leituras enviadas pela esteira.
 */
@Service
public class LeituraService {

    private final LeituraPecaRepository leituraPecaRepository;
    private final LeituraNivelRepository leituraNivelRepository;
    private final EsteiraService esteiraService;
    private final ClassificadorPeca classificadorPeca;
    private final AlertaService alertaService;

    public LeituraService(LeituraPecaRepository leituraPecaRepository,
                          LeituraNivelRepository leituraNivelRepository,
                          EsteiraService esteiraService,
                          ClassificadorPeca classificadorPeca,
                          AlertaService alertaService) {
        this.leituraPecaRepository = leituraPecaRepository;
        this.leituraNivelRepository = leituraNivelRepository;
        this.esteiraService = esteiraService;
        this.classificadorPeca = classificadorPeca;
        this.alertaService = alertaService;
    }

    /**
     * Fluxo de uma peça: esteira ativa -> classificação (Strategy) -> salvar -> avaliar alerta.
     */
    @Transactional
    public LeituraResponse registrarPeca(LeituraPecaRequest request) {
        Esteira esteira = buscarEsteiraAtiva(request.esteiraId());

        TipoPeca tipo = classificadorPeca.classificar(request.sensorIndutivo(), request.sensorCapacitivo());

        LeituraPeca leitura = leituraPecaRepository.save(new LeituraPeca(
                esteira,
                request.registradoEm(),
                request.sensorIndutivo(),
                request.sensorCapacitivo(),
                tipo
        ));

        alertaService.avaliarEGerar(leitura);
        return LeituraResponse.de(leitura);
    }

    /**
     * Fluxo do nível: esteira ativa -> cálculo do percentual (na entidade) -> salvar -> avaliar alerta.
     */
    @Transactional
    public LeituraResponse registrarNivel(LeituraNivelRequest request) {
        Esteira esteira = buscarEsteiraAtiva(request.esteiraId());

        LeituraNivel leitura = leituraNivelRepository.save(new LeituraNivel(
                esteira,
                request.registradoEm(),
                request.distanciaCm()
        ));

        alertaService.avaliarEGerar(leitura);
        return LeituraResponse.de(leitura);
    }

    @Transactional(readOnly = true)
    public Page<LeituraResponse> ultimasPecas(Long esteiraId, Pageable pageable) {
        esteiraService.buscarEntidade(esteiraId);
        return leituraPecaRepository.findByEsteiraIdOrderByRegistradoEmDesc(esteiraId, pageable)
                .map(LeituraResponse::de);
    }

    @Transactional(readOnly = true)
    public Page<LeituraResponse> ultimosNiveis(Long esteiraId, Pageable pageable) {
        esteiraService.buscarEntidade(esteiraId);
        return leituraNivelRepository.findByEsteiraIdOrderByRegistradoEmDesc(esteiraId, pageable)
                .map(LeituraResponse::de);
    }

    /**
     * Histórico completo (peças + nível) em ordem cronológica.
     * Base dos gráficos e da exportação CSV para a Ciência de Dados.
     */
    @Transactional(readOnly = true)
    public List<LeituraResponse> historico(Long esteiraId, LocalDateTime inicio, LocalDateTime fim) {
        validarPeriodo(inicio, fim);
        esteiraService.buscarEntidade(esteiraId);

        Stream<LeituraResponse> pecas = leituraPecaRepository
                .findByEsteiraIdAndRegistradoEmBetweenOrderByRegistradoEmAsc(esteiraId, inicio, fim)
                .stream()
                .map(LeituraResponse::de);

        Stream<LeituraResponse> niveis = leituraNivelRepository
                .findByEsteiraIdAndRegistradoEmBetweenOrderByRegistradoEmAsc(esteiraId, inicio, fim)
                .stream()
                .map(LeituraResponse::de);

        return Stream.concat(pecas, niveis)
                .sorted(Comparator.comparing(LeituraResponse::registradoEm))
                .toList();
    }

    private Esteira buscarEsteiraAtiva(Long esteiraId) {
        Esteira esteira = esteiraService.buscarEntidade(esteiraId);
        if (!esteira.isAtiva()) {
            throw new LeituraInvalidaException(
                    "A esteira '" + esteira.getNome() + "' está desativada e não aceita leituras.");
        }
        return esteira;
    }

    private void validarPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Informe o início e o fim do período.");
        }
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("O início do período deve ser anterior ao fim.");
        }
    }
}