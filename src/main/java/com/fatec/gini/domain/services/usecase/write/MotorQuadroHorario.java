package com.fatec.gini.domain.services.usecase.write;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.dto.grade.CursoGradeResumoResponse;
import com.fatec.gini.dto.grade.DisciplinaGradeResumoResponse;
import com.fatec.gini.dto.grade.GradeCursoResponse;
import com.fatec.gini.dto.grade.QuadroHorarioGradeResumoResponse;
import com.fatec.gini.dto.grade.SalaGradeResumoResponse;
import com.fatec.gini.dto.quadroHorario.BlocoQuadroResponse;
import com.fatec.gini.dto.quadroHorario.CelulaGradeResponse;
import com.fatec.gini.dto.quadroHorario.TurmaQuadroResponse;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;
import com.fatec.gini.web.exception.BusinessException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MotorQuadroHorario {

        private final CursoRepository cursoRepository;
        private final QuadroHorarioRepository quadroHorarioRepository;
        private final TurmaRepository turmaRepository;
        private final AlocacaoRepository alocacaoRepository;

        public GradeCursoResponse contruir(Long cursoId) {
                Curso curso = cursoRepository.findById(cursoId)
                                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

                QuadroHorario quadroAtivo = buscarQuadroAtivo(cursoId);

                // 1. Busca todas as alocações da grade
                List<Alocacao> alocacoes = alocacaoRepository.buscarAlocacoesGradeCurso(cursoId, quadroAtivo.getId());

                // 2. Extrai os Blocos Horários (Linhas da tabela)
                List<BlocoHorario> blocosUnicos = alocacoes.stream()
                                .map(Alocacao::getBlocoHorario)
                                .filter(Objects::nonNull)
                                .distinct()
                                .sorted(Comparator.comparing(BlocoHorario::getHoraInicio))
                                .toList();

                List<BlocoQuadroResponse> blocosResponse = blocosUnicos.stream()
                                .map(this::converterBlocoGradeResponse)
                                .toList();

                // 3. Extrai os Dias da Semana ativos neste curso (Colunas da tabela)
                var diasAtivos = alocacoes.stream()
                                .map(Alocacao::getDiaSemana)
                                .filter(Objects::nonNull)
                                .distinct()
                                .sorted() // O Enum de dias já deve respeitar a ordem cronológica
                                .toList();

                List<Turma> turmas = turmaRepository.findByCursoIdOrderByAnoAscPeriodoAscCodigoAsc(cursoId);
                // percorre as alocações e agrupa pelo id da turma. Chave: o id da turma, valor:
                // lista de alocações
                Map<Long, List<Alocacao>> alocacoesPorTurma = alocacoes.stream()
                                .collect(Collectors.groupingBy(a -> a.getTurma().getId()));

                // 4. Monta as turmas gerando a matriz completa (Dias x Blocos)
                List<TurmaQuadroResponse> turmasResponse = turmas.stream().map(
                                turma -> {
                                        List<Alocacao> alocacoesDaTurma = alocacoesPorTurma.getOrDefault(turma.getId(),
                                                        List.of());
                                        List<CelulaGradeResponse> celulas = new ArrayList<>();

                                        // Cruza todos os dias com todos os blocos para formar o "grid" da tabela
                                        for (var dia : diasAtivos) {
                                                for (BlocoHorario bloco : blocosUnicos) {

                                                        // Tenta encontrar a aula específica para este dia e horário
                                                        alocacoesDaTurma.stream()
                                                                        .filter(a -> a.getDiaSemana().equals(dia) && a
                                                                                        .getBlocoHorario().getId()
                                                                                        .equals(bloco.getId()))
                                                                        .findFirst()
                                                                        .ifPresentOrElse(
                                                                                        alocacao -> celulas.add(
                                                                                                        converterCelulaGradeResponse(
                                                                                                                        alocacao)),
                                                                                        () -> celulas.add(
                                                                                                        criarCelulaVazia(
                                                                                                                        dia,
                                                                                                                        bloco.getId())) // Célula
                                                                                                                                        // vazia
                                                                                                                                        // para
                                                                                                                                        // manter
                                                                                                                                        // a
                                                                                                                                        // estrutura
                                                        );
                                                }
                                        }

                                        return new TurmaQuadroResponse(turma.getId(),
                                                        turma.getCodigo(),
                                                        turma.getAno(),
                                                        turma.getPeriodo(),
                                                        celulas);
                                }).toList();

                return new GradeCursoResponse(
                                new CursoGradeResumoResponse(curso.getId(), curso.getNome()),
                                new QuadroHorarioGradeResumoResponse(quadroAtivo.getId(), quadroAtivo.getVersao(),
                                                quadroAtivo.getStatus()),
                                blocosResponse,
                                turmasResponse);
        }

        private CelulaGradeResponse criarCelulaVazia(DiaSemana diaSemana, Long blocoHorarioId) {
                return new CelulaGradeResponse(
                                diaSemana,
                                blocoHorarioId,
                                null, // Sem disciplina
                                null, // Sem sala
                                null // Sem id de alocação
                );
        }

     
        private QuadroHorario buscarQuadroAtivo(Long cursoId) {
                List<QuadroHorario> quadrosAtivos = quadroHorarioRepository.findByCursoIdAndStatus(cursoId,
                                Status.ATIVO);

                if (quadrosAtivos.isEmpty()) {
                        throw new EntityNotFoundException("Quadro horário ativo não encontrado para o curso");
                }

                if (quadrosAtivos.size() > 1) {
                        throw new BusinessException(
                                        "Existe mais de um quadro de horário ativo para o curso informado.");
                }

                return quadrosAtivos.get(0);
        }

        private BlocoQuadroResponse converterBlocoGradeResponse(BlocoHorario bloco) {
                return new BlocoQuadroResponse(
                                bloco.getId(),
                                bloco.getHoraInicio(),
                                bloco.getHoraFim(),
                                formatarRotulo(bloco.getHoraInicio(), bloco.getHoraFim()));
        }

        private CelulaGradeResponse converterCelulaGradeResponse(Alocacao alocacao) {
                return new CelulaGradeResponse(
                                alocacao.getDiaSemana(),
                                alocacao.getBlocoHorario().getId(),
                                new DisciplinaGradeResumoResponse(
                                                alocacao.getDisciplina().getId(),
                                                alocacao.getDisciplina().getNome()),
                                new SalaGradeResumoResponse(
                                                alocacao.getSala().getId(),
                                                alocacao.getSala().getCodigo()),
                                alocacao.getId());
        }

        private String formatarRotulo(LocalTime horaInicio, LocalTime horaFim) {
                if (horaInicio == null || horaFim == null) {
                        return "";
                }
                return String.format("%02dh%02d-%02dh%02d", horaInicio.getHour(), horaInicio.getMinute(),
                                horaFim.getHour(),
                                horaFim.getMinute());
        }
}
