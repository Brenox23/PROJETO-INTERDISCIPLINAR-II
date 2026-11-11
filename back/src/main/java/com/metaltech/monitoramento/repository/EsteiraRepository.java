package com.metaltech.monitoramento.repository;

import com.metaltech.monitoramento.domain.model.Esteira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EsteiraRepository extends JpaRepository<Esteira, Long> {

    Optional<Esteira> findByNome(String nome);

    boolean existsByNome(String nome);

    List<Esteira> findByAtivaTrue();
}