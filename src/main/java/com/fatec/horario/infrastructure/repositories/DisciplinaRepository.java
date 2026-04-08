package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fatec.horario.domain.entities.Disciplina;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    
    Page<Disciplina> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Disciplina> findByCodDisciplinaContainingIgnoreCase(String codDisciplina, Pageable pageable);
    Page<Disciplina> findByTipoDisciplinaContainingIgnoreCase(String tipoDisciplina, Pageable pageable);
    Page<Disciplina> findByModalidadeContainingIgnoreCase(String modalidade, Pageable pageable);
    Page<Disciplina> findByCursoIdCursoContainingIgnoreCase(Long idCurso, Pageable pageable);
    Page<Disciplina> findByTipoSalaIdTipoSalaContainingIgnoreCase(Long idTipoSala, Pageable pageable);
    Page<Disciplina> findByNomeContainingIgnoreCaseAndCursoIdCursoAndTipoSalaIdTipoSala(String nome, Long idCurso, Long idTipoSala, Pageable pageable);
}

