package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.horario.domain.entities.Alocacao;


public interface AlocacaoRepository extends JpaRepository<Alocacao, Long> {

        /**
         * REGRA DE CONFLITO: "A sala já está ocupada?"
         * Este método pergunta ao banco de dados: "Existe algum registro onde esta
         * SALA,
         * neste DIA e neste HORÁRIO já apareça?". Se sim, ele responde TRUE
         * (verdadeiro).
         */
        boolean existsBySalaIdAndDiaSemanaIdAndHorarioId(Long salaId, Long diaId, Long horarioId);

        /**
         * REGRA DE CONFLITO: "O professor já está dando aula?"
         * Mesma lógica: pergunta se o PROFESSOR (usuário) já tem algo marcado para
         * aquele exato momento.
         */
        boolean existsByUsuarioIdAndDiaSemanaIdAndHorarioId(Long usuarioId, Long diaId, Long horarioId);

        /**
         * REGRA DE DUPLICIDADE: "A alocação já existe com os mesmos vínculos?"
         * Verifica se já existe alocação com os mesmos ids de turma, disciplina, sala,
         * dia da semana e horário.
         */
        boolean existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioId(
                        Long turmaId,
                        Long disciplinaId,
                        Long salaId,
                        Long diaSemanaId,
                        Long horarioId);

        /**
         * REGRA DE DUPLICIDADE (ATUALIZAÇÃO):
         * mesma verificação de duplicidade, desconsiderando a própria alocação em edição.
         */
        boolean existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioIdAndIdNot(
                        Long turmaId,
                        Long disciplinaId,
                        Long salaId,
                        Long diaSemanaId,
                        Long horarioId,
                        Long alocacaoId);

        /**
         * REGRA DE DISPONIBILIDADE: "O professor pode trabalhar agora?"
         * A regra verifica se existe um registro prévio que autorize a alocação do usuário para o par DiaSemana/Horário informado.
         * retorna true se o contador for > 0 (possui permissão), false caso contrário.
         */
        @Query("SELECT COUNT(d) > 0 FROM DisponibilidadeProfessor d WHERE d.usuario.id = :usuarioId AND d.diaSemana.id = :diaId AND d.horario.id = :horarioId")
        boolean verificarDisponibilidadeProfessor(
                        @Param("usuarioId") Long usuarioId,
                        @Param("diaId") Long diaId,
                        @Param("horarioId") Long horarioId);

        /* busca personalizada: */

        @Query("SELECT a FROM Alocacao a WHERE " +
                        "(:turmaId IS NULL OR a.turma.id = :turmaId) AND " +
                        "(:disciplinaId IS NULL OR a.disciplina.id = :disciplinaId) AND " +
                        "(:salaId IS NULL OR a.sala.id = :salaId) AND " +
                        "(:usuarioId IS NULL OR a.usuario.id = :usuarioId) AND " +
                        "(:diaSemanaId IS NULL OR a.diaSemana.id = :diaSemanaId) AND " +
                        "(:horarioId IS NULL OR a.horario.id = :horarioId) AND " +
                        "(:gradeId IS NULL OR a.gradeHoraria.id = :gradeId)")
        Page<Alocacao> buscarPorFiltros(
                        @Param("turmaId") Long turmaId,
                        @Param("disciplinaId") Long disciplinaId,
                        @Param("salaId") Long salaId,
                        @Param("usuarioId") Long usuarioId,
                        @Param("diaSemanaId") Long diaSemanaId,
                        @Param("horarioId") Long horarioId,
                        @Param("gradeId") Long gradeId,
                        Pageable pageable);
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