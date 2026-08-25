package com.fatec.gini.infrastructure.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DiaSemana;

public interface AlocacaoRepository extends JpaRepository<Alocacao, Long> {

    /**
     * REGRA DE CONFLITO: "A sala já está ocupada?"
     * Este método pergunta ao banco de dados: "Existe algum registro onde esta
     * SALA,
     * neste DIA e neste HORÁRIO já apareça?". Se sim, ele responde TRUE
     * (verdadeiro).
     */
    boolean existsBySalaIdAndDiaSemanaAndBlocoHorarioId(Long salaId, DiaSemana diaSemana, Long blocoHorarioId);

    /**
     * REGRA DE CONFLITO: "O professor já está dando aula?"
     * Mesma lógica: pergunta se o PROFESSOR (professor) já tem algo marcado para
     * aquele exato momento.
     */
    boolean existsByProfessorIdAndDiaSemanaAndBlocoHorarioId(Long professorId, DiaSemana diaSemana,
            Long blocoHorarioId);

    /**
     * REGRA DE DUPLICIDADE: "A alocação já existe com os mesmos vínculos?"
     * Verifica se já existe alocação com os mesmos ids de turma, disciplina, sala,
     * dia da semana e horário.
     */
    boolean existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaAndBlocoHorarioId(
            Long turmaId,
            Long disciplinaId,
            Long salaId,
            DiaSemana diaSemana,
            Long blocoHorarioId);

    /**
     * REGRA DE DUPLICIDADE (ATUALIZAÇÃO):
     * mesma verificação de duplicidade, desconsiderando a própria alocação em
     * edição.
     */
    boolean existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaAndBlocoHorarioIdAndIdNot(
            Long turmaId,
            Long disciplinaId,
            Long salaId,
            DiaSemana diaSemana,
            Long blocoHorarioId,
            Long alocacaoId);

    /* busca personalizada: */
    @Query("SELECT a FROM Alocacao a WHERE " +
            "(:turmaId IS NULL OR a.turma.id = :turmaId) AND " +
            "(:disciplinaId IS NULL OR a.disciplina.id = :disciplinaId) AND " +
            "(:salaId IS NULL OR a.sala.id = :salaId) AND " +
            "(:professorId IS NULL OR a.professor.id = :professorId) AND " +
            "(:diaSemana IS NULL OR a.diaSemana = :diaSemana) AND " +
            "(:blocoHorarioId IS NULL OR a.blocoHorario.id = :blocoHorarioId) AND " +
            "(:quadroHorarioId IS NULL OR a.quadroHorario.id = :quadroHorarioId)")
    Page<Alocacao> buscarPorFiltros(
            @Param("turmaId") Long turmaId,
            @Param("disciplinaId") Long disciplinaId,
            @Param("salaId") Long salaId,
            @Param("professorId") Long professorId,
            @Param("diaSemana") DiaSemana diaSemana,
            @Param("blocoHorarioId") Long blocoHorarioId,
            @Param("quadroHorarioId") Long quadroHorarioId,
            Pageable pageable);

    @Query("""
                SELECT COUNT(a) > 0
                FROM Alocacao a
                WHERE a.turma.id = :idTurma
                AND a.diaSemana = :diaSemana
                AND a.blocoHorario.id = :idBlocoHorario
            """)
    boolean existsConflitoTurmaHorario(
            @Param("idTurma") Long idTurma,
            @Param("diaSemana") DiaSemana diaSemana,
            @Param("idBlocoHorario") Long idBlocoHorario);

    /* existence checks used by delete validations */
    boolean existsByDisciplinaId(Long disciplinaId);

    boolean existsBySalaId(Long salaId);

    boolean existsByTurmaId(Long turmaId);

    boolean existsByProfessorId(Long professorId);

    /* metodo para a implementação da validação das 12h e carga horaria maxima: */
    List<Alocacao> findByProfessorIdAndDiaSemana(
        Long professorId,
        DiaSemana diaSemana);

    @Query("""
                SELECT a
                FROM Alocacao a
                WHERE a.professor.id = :professorId
                  AND a.diaSemana = :diaSemana
                ORDER BY a.blocoHorario.horaFim DESC
            """)
    List<Alocacao> buscarUltimaAulaDoDia(
            Long professorId,
            DiaSemana diaSemana);

    List<Alocacao> findByQuadroHorarioId(
            Long quadroHorarioId);

    // Para validar a carga horária total da disciplina no quadro horário,
    // precisamos
    // somar a duração de todas as alocações daquela disciplina e quadro horário:

    @Query("""
            SELECT COALESCE(SUM(a.blocoHorario.duracao), 0)
            FROM Alocacao a
            WHERE a.disciplina.id = :disciplinaId
            AND a.quadroHorario.id = :quadroHorarioId
            """)
    int somarDuracaoPorDisciplinaEQuadro(
            @Param("disciplinaId") Long disciplinaId,
            @Param("quadroHorarioId") Long quadroHorarioId);

    @Query("""
            SELECT COALESCE(SUM(a.blocoHorario.duracao), 0)
            FROM Alocacao a
            WHERE a.disciplina.id = :disciplinaId
            AND a.quadroHorario.id = :quadroHorarioId
            AND a.id <> :alocacaoId
            """)
    int somarDuracaoPorDisciplinaEQuadroHorarioEIdNot(
            @Param("disciplinaId") Long disciplinaId,
            @Param("quadroHorarioId") Long quadroHorarioId,
            @Param("alocacaoId") Long alocacaoId);

}