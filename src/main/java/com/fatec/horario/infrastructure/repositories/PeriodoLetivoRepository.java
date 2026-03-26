package com.fatec.horario.infrastructure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.PeriodoLetivo;

@Repository
public interface PeriodoLetivoRepository extends JpaRepository<PeriodoLetivo, Long> {
    
    List<PeriodoLetivo> findByAno(Integer ano);

    List<PeriodoLetivo> findByPeriodoContainingIgnoreCase(String periodo); //usei o containig ignore case para nao ter restriçoes na hora de pesquisar os periodos

    List<PeriodoLetivo> findByStatus(String status);

    List<PeriodoLetivo> findByAnoAndPeriodoAndStatus(Integer ano, String periodo, String status);

}
