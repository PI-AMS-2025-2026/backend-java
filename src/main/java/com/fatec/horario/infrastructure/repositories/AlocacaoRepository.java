package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.Alocacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// classe não existe até o momento que estou fazendo a issue
public interface AlocacaoRepository extends JpaRepository<Alocacao, Long> {

    @Query("""
        SELECT COUNT(a) > 0
        FROM Alocacao a
        WHERE a.turma.id_turma = :idTurma
        AND a.diaSemana.id_dia_semana = :idDiaSemana
        AND a.horario.id_horario = :idHorario
    """)
    boolean existsConflitoTurmaHorario(
            @Param("idTurma") Long idTurma,
            @Param("idDiaSemana") Long idDiaSemana,
            @Param("idHorario") Long idHorario
    );
}