package com.fatec.horario.infrastructure.repositories;

//classe Alocacao.java não existe até o momento
import com.fatec.horario.domain.entities.Alocacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlocacaoRepository extends JpaRepository<Alocacao, Long> {

    @Query("""
        SELECT COUNT(a) > 0
        FROM Alocacao a
        WHERE a.turma.id = :idTurma
        AND a.diaSemana.id = :idDiaSemana
        AND a.horario.id = :idHorario
    """)
    boolean existsConflitoTurmaHorario(
            @Param("idTurma") Long idTurma,
            @Param("idDiaSemana") Long idDiaSemana,
            @Param("idHorario") Long idHorario
    );
}