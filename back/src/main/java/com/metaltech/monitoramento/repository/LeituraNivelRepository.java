package com.metaltech.monitoramento.repository;

import com.metaltech.monitoramento.domain.model.LeituraNivel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeituraNivelRepository extends JpaRepository<LeituraNivel, Long> {

    /** Leitura mais recente do reservatório (dashboard e RA). */
    Optional<LeituraNivel> findFirstByEsteiraIdOrderByRegistradoEmDesc(Long esteiraId);

    /** Histórico do nível em um período (gráficos e exportação CSV). */
    List<LeituraNivel> findByEsteiraIdAndRegistradoEmBetweenOrderByRegistradoEmAsc(
            Long esteiraId, LocalDateTime inicio, LocalDateTime fim);

    /** Últimas leituras de nível, paginadas. */
    Page<LeituraNivel> findByEsteiraIdOrderByRegistradoEmDesc(Long esteiraId, Pageable pageable);
}