package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * Valida:
 * </p>
 * <ul>
 * <li>professor tem disponibilidade no dia e horário</li>
 * <li>professor não foi alocado em duas aulas no mesmo slot</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ValidarDisponibilidadeProfessorUseCase {

    private final  AlocacaoRepository alocacaoRepository;

    private final  DisponibilidadeProfessorRepository disponibilidadeProfessorRepository;

    public void executar(Alocacao entity) {
        var diaSemana = entity.getDiaSemana().getId();
        var horario = entity.getBlocoHorario().getId();
        var professorId = entity.getProfessor().getId();

        temDisponibilidadeDiaHorario(professorId, diaSemana, horario);
        temSobreposicaoAtividades(professorId, diaSemana, horario);
    }

    private void temDisponibilidadeDiaHorario(Long professorId, Long diaId, Long horarioId) {
        boolean possuiDisponibilidade = disponibilidadeProfessorRepository
                .verificarDisponibilidadeProfessor(professorId, diaId, horarioId);

        if (!possuiDisponibilidade) {
            throw new BusinessException("Professor não possui disponibilidade para o dia e horário informado.");
        }
    }

    private void temSobreposicaoAtividades(Long professorId, Long diaId, Long horarioId) {
        boolean temSobreposicao = alocacaoRepository
                .existsByProfessorIdAndDiaSemanaIdAndBlocoHorarioId(
                        professorId, diaId, horarioId);

        if (temSobreposicao) {
            throw new BusinessException("Professor já está alocado em outra turma/disciplina neste dia e horário.");
        }
    }
}
