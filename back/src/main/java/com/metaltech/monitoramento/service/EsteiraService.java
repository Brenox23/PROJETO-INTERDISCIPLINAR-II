package com.metaltech.monitoramento.service;

import com.metaltech.monitoramento.domain.model.Esteira;
import com.metaltech.monitoramento.dto.request.EsteiraRequest;
import com.metaltech.monitoramento.dto.response.EsteiraResponse;
import com.metaltech.monitoramento.exception.RecursoNaoEncontradoException;
import com.metaltech.monitoramento.repository.EsteiraRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de negócio do cadastro de esteiras (CRUD).
 */
@Service
public class EsteiraService {

    private final EsteiraRepository esteiraRepository;

    public EsteiraService(EsteiraRepository esteiraRepository) {
        this.esteiraRepository = esteiraRepository;
    }

    @Transactional
    public EsteiraResponse criar(EsteiraRequest request) {
        if (esteiraRepository.existsByNome(request.nome().trim())) {
            throw new IllegalStateException("Já existe uma esteira com o nome '" + request.nome().trim() + "'.");
        }
        Esteira esteira = new Esteira(
                request.nome(),
                request.localizacao(),
                request.alturaReservatorioCm(),
                request.limiteAtencaoPercentual(),
                request.limiteCriticoPercentual()
        );
        return EsteiraResponse.de(esteiraRepository.save(esteira));
    }

    @Transactional(readOnly = true)
    public List<EsteiraResponse> listar() {
        return esteiraRepository.findAll(Sort.by("nome")).stream()
                .map(EsteiraResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public EsteiraResponse buscarPorId(Long id) {
        return EsteiraResponse.de(buscarEntidade(id));
    }

    @Transactional
    public EsteiraResponse atualizar(Long id, EsteiraRequest request) {
        Esteira esteira = buscarEntidade(id);

        boolean nomeEmUsoPorOutra = esteiraRepository.findByNome(request.nome().trim())
                .filter(outra -> !outra.getId().equals(id))
                .isPresent();
        if (nomeEmUsoPorOutra) {
            throw new IllegalStateException("Já existe outra esteira com o nome '" + request.nome().trim() + "'.");
        }

        esteira.atualizarDados(
                request.nome(),
                request.localizacao(),
                request.alturaReservatorioCm(),
                request.limiteAtencaoPercentual(),
                request.limiteCriticoPercentual()
        );
        return EsteiraResponse.de(esteira);
    }

    /**
     * "Exclusão lógica": a esteira é desativada, mas o histórico de leituras é preservado
     * (essencial para a análise de dados).
     */
    @Transactional
    public void desativar(Long id) {
        buscarEntidade(id).desativar();
    }

    @Transactional
    public EsteiraResponse ativar(Long id) {
        Esteira esteira = buscarEntidade(id);
        esteira.ativar();
        return EsteiraResponse.de(esteira);
    }

    /**
     * Usado pelos outros services quando precisam da entidade (e não do DTO).
     */
    @Transactional(readOnly = true)
    public Esteira buscarEntidade(Long id) {
        return esteiraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esteira", id));
    }
}