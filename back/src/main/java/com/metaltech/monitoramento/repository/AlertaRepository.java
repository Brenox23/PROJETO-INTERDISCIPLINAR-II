package com.metaltech.monitoramento.repository;

import com.metaltech.monitoramento.domain.model.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    /** Alertas ainda abertos de uma esteira (indicador visual na RA). */
    List<Alerta> findByEsteiraIdAndResolvidoFalseOrderByCriadoEmDesc(Long esteiraId);

    /** Todos os alertas abertos da planta. */
    List<Alerta> findByResolvidoFalseOrderByCriadoEmDesc();

    /** Histórico de alertas de uma esteira, paginado. */
    Page<Alerta> findByEsteiraIdOrderByCriadoEmDesc(Long esteiraId, Pageable pageable);
}