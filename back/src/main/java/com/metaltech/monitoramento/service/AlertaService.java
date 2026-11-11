package com.metaltech.monitoramento.service;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import com.metaltech.monitoramento.domain.model.Alerta;
import com.metaltech.monitoramento.domain.model.Leitura;
import com.metaltech.monitoramento.domain.model.LeituraNivel;
import com.metaltech.monitoramento.dto.response.AlertaResponse;
import com.metaltech.monitoramento.exception.RecursoNaoEncontradoException;
import com.metaltech.monitoramento.repository.AlertaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Geração e gestão dos alertas da plataforma.
 */
@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    /**
     * POLIMORFISMO: recebe qualquer Leitura (peça ou nível) e pergunta
     * a ela mesma se precisa de alerta, sem usar if/else por tipo.
     */
    @Transactional
    public Optional<Alerta> avaliarEGerar(Leitura leitura) {
        NivelAlerta nivel = leitura.avaliarAlerta();
        if (!nivel.exigeAtencao()) {
            return Optional.empty();
        }

        // Evita alertas repetidos enquanto o reservatório continua no mesmo nível.
        if (leitura instanceof LeituraNivel
                && alertaRepository.existeAlertaDeNivelAberto(leitura.getEsteira().getId(), nivel)) {
            return Optional.empty();
        }

        return Optional.of(alertaRepository.save(Alerta.gerarDe(leitura)));
    }

    /**
     * Alertas abertos. Se esteiraId for nulo, traz os de todas as esteiras.
     */
    @Transactional(readOnly = true)
    public List<AlertaResponse> listarAbertos(Long esteiraId) {
        List<Alerta> alertas = (esteiraId == null)
                ? alertaRepository.findByResolvidoFalseOrderByCriadoEmDesc()
                : alertaRepository.findByEsteiraIdAndResolvidoFalseOrderByCriadoEmDesc(esteiraId);
        return alertas.stream().map(AlertaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public Page<AlertaResponse> historico(Long esteiraId, Pageable pageable) {
        return alertaRepository.findByEsteiraIdOrderByCriadoEmDesc(esteiraId, pageable)
                .map(AlertaResponse::de);
    }

    @Transactional
    public AlertaResponse resolver(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta", id));
        alerta.resolver();
        return AlertaResponse.de(alerta);
    }
}