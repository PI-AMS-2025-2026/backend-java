package com.fatec.gini.infrastructure.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.Alocacao;

public interface AlocacaoRepository extends JpaRepository<Alocacao, Long> {

        /**
         * REGRA DE CONFLITO: "A sala já está ocupada?"
         * Este método pergunta ao banco de dados: "Existe algum registro onde esta
         * SALA,
         * neste DIA e neste HORÁRIO já apareça?". Se sim, ele responde TRUE
         * (verdadeiro).
         */
        boolean existsBySalaIdAndDiaSemanaIdAndBlocoHorarioId(Long salaId, Long diaId, Long blocoHorarioId);

        /**
         * REGRA DE CONFLITO: "O professor já está dando aula?"
         * Mesma lógica: pergunta se o PROFESSOR (professor) já tem algo marcado para
         * aquele exato momento.
         */
        boolean existsByProfessorIdAndDiaSemanaIdAndBlocoHorarioId(Long professorId, Long diaId, Long blocoHorarioId);

        /**
         * REGRA DE DUPLICIDADE: "A alocação já existe com os mesmos vínculos?"
         * Verifica se já existe alocação com os mesmos ids de turma, disciplina, sala,
         * dia da semana e horário.
         */
        boolean existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndBlocoHorarioId(
                        Long turmaId,
                        Long disciplinaId,
                        Long salaId,
                        Long diaSemanaId,
                        Long blocoHorarioId);

        /**
         * REGRA DE DUPLICIDADE (ATUALIZAÇÃO):
         * mesma verificação de duplicidade, desconsiderando a própria alocação em
         * edição.
         */
        boolean existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndBlocoHorarioIdAndIdNot(
                        Long turmaId,
                        Long disciplinaId,
                        Long salaId,
                        Long diaSemanaId,
                        Long blocoHorarioId,
                        Long alocacaoId);

        /* busca personalizada: */
        @Query("SELECT a FROM Alocacao a WHERE " +
                        "(:turmaId IS NULL OR a.turma.id = :turmaId) AND " +
                        "(:disciplinaId IS NULL OR a.disciplina.id = :disciplinaId) AND " +
                        "(:salaId IS NULL OR a.sala.id = :salaId) AND " +
                        "(:professorId IS NULL OR a.professor.id = :professorId) AND " +
                        "(:diaSemanaId IS NULL OR a.diaSemana.id = :diaSemanaId) AND " +
                        "(:blocoHorarioId IS NULL OR a.blocoHorario.id = :blocoHorarioId) AND " +
                        "(:gradeId IS NULL OR a.gradeHoraria.id = :gradeId)")
        Page<Alocacao> buscarPorFiltros(
                        @Param("turmaId") Long turmaId,
                        @Param("disciplinaId") Long disciplinaId,
                        @Param("salaId") Long salaId,
                        @Param("professorId") Long professorId,
                        @Param("diaSemanaId") Long diaSemanaId,
                        @Param("blocoHorarioId") Long blocoHorarioId,
                        @Param("gradeId") Long gradeId,
                        Pageable pageable);

        @Query("""
                            SELECT COUNT(a) > 0
                            FROM Alocacao a
                            WHERE a.turma.id = :idTurma
                            AND a.diaSemana.id = :idDiaSemana
                            AND a.blocoHorario.id = :idBlocoHorario
                        """)
        boolean existsConflitoTurmaHorario(
                        @Param("idTurma") Long idTurma,
                        @Param("idDiaSemana") Long idDiaSemana,
                        @Param("idBlocoHorario") Long idBlocoHorario);

        /* existence checks used by delete validations */
        boolean existsByDisciplinaId(Long disciplinaId);

        boolean existsBySalaId(Long salaId);

        boolean existsByTurmaId(Long turmaId);

        boolean existsByProfessorId(Long professorId);

        /* metodo para a implementação da validação das 12h e carga horaria maxima: */

        @Query("""
                            SELECT a
                            FROM Alocacao a
                            WHERE a.professor.id = :professorId
                            AND a.diaSemana.id = :diaSemanaId
                        """)
        List<Alocacao> findByProfessorAndDiaSemana(
                        Long professorId,
                        Long diaSemanaId);

        @Query("""
                            SELECT a
                            FROM Alocacao a
                            WHERE a.professor.id = :professorId
                            AND a.diaSemana.id = :diaSemanaId
                            ORDER BY a.blocoHorario.horaFim DESC
                        """)
        List<Alocacao> findUltimaAulaDoDia(
                        Long professorId,
                        Long diaSemanaId);

        List<Alocacao> findByGradeHorariaId(
                        Long gradeHorariaId);

        // Para validar a carga horária total da disciplina na grade horária, precisamos
        // somar a duração de todas as alocações daquela disciplina e grade horária:

        @Query("""
                        SELECT COALESCE(SUM(a.blocoHorario.duracao), 0)
                        FROM Alocacao a
                        WHERE a.disciplina.id = :disciplinaId
                        AND a.gradeHoraria.id = :gradeHorariaId
                        """)
        int somarDuracaoPorDisciplinaEGrade(
                        @Param("disciplinaId") Long disciplinaId,
                        @Param("gradeHorariaId") Long gradeHorariaId);

        @Query("""
                        SELECT COALESCE(SUM(a.blocoHorario.duracao), 0)
                        FROM Alocacao a
                        WHERE a.disciplina.id = :disciplinaId
                        AND a.gradeHoraria.id = :gradeHorariaId
                        AND a.id <> :alocacaoId
                        """)
        int somarDuracaoPorDisciplinaEGradeEIdNot(
                        @Param("disciplinaId") Long disciplinaId,
                        @Param("gradeHorariaId") Long gradeHorariaId,
                        @Param("alocacaoId") Long alocacaoId);
}