package com.metaltech.monitoramento.repository;

import com.metaltech.monitoramento.domain.enums.TipoPeca;
import com.metaltech.monitoramento.domain.model.LeituraPeca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LeituraPecaRepository extends JpaRepository<LeituraPeca, Long> {

    /** Contagem total de peças de um tipo em uma esteira. */
    long countByEsteiraIdAndTipo(Long esteiraId, TipoPeca tipo);

    /** Contagem de peças de um tipo em um período (ex.: turno, dia). */
    long countByEsteiraIdAndTipoAndRegistradoEmBetween(
            Long esteiraId, TipoPeca tipo, LocalDateTime inicio, LocalDateTime fim);

    /** Histórico completo em um período (gráficos e exportação CSV). */
    List<LeituraPeca> findByEsteiraIdAndRegistradoEmBetweenOrderByRegistradoEmAsc(
            Long esteiraId, LocalDateTime inicio, LocalDateTime fim);

    /** Últimas leituras, paginadas (listagem no dashboard). */
    Page<LeituraPeca> findByEsteiraIdOrderByRegistradoEmDesc(Long esteiraId, Pageable pageable);
}