package com.fatec.horario.infrastructure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.PeriodoLetivo;

@Repository
public interface PeriodoLetivoRepository extends JpaRepository<PeriodoLetivo, Long> {

    List<PeriodoLetivo> findByAno(Integer ano);

    List<PeriodoLetivo> findByPeriodo(Integer periodo);

    List<PeriodoLetivo> findByStatus(String status);

    List<PeriodoLetivo> findByAnoAndPeriodoAndStatus(Integer ano, Integer periodo, String status);
}
