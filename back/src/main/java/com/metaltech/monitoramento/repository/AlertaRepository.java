package com.metaltech.monitoramento.repository;

import com.metaltech.monitoramento.domain.enums.NivelAlerta;
import com.metaltech.monitoramento.domain.model.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    /** Alertas ainda abertos de uma esteira (indicador visual na RA). */
    List<Alerta> findByEsteiraIdAndResolvidoFalseOrderByCriadoEmDesc(Long esteiraId);

    /** Todos os alertas abertos da planta. */
    List<Alerta> findByResolvidoFalseOrderByCriadoEmDesc();

    /** Histórico de alertas de uma esteira, paginado. */
    Page<Alerta> findByEsteiraIdOrderByCriadoEmDesc(Long esteiraId, Pageable pageable);

    /**
     * Verifica se já existe um alerta ABERTO de nível do reservatório
     * com o mesmo nível de gravidade. Evita gerar um alerta novo
     * a cada leitura enquanto o reservatório continua cheio.
     */
    @Query("""
            select case when count(a) > 0 then true else false end
            from Alerta a
            join a.leitura l
            where a.esteira.id = :esteiraId
              and a.nivel = :nivel
              and a.resolvido = false
              and type(l) = LeituraNivel
            """)
    boolean existeAlertaDeNivelAberto(@Param("esteiraId") Long esteiraId,
                                      @Param("nivel") NivelAlerta nivel);
}