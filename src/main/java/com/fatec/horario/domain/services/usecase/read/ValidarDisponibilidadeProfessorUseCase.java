package com.fatec.horario.domain.services.usecase.read;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.horario.web.exception.BusinessException;

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
public class ValidarDisponibilidadeProfessorUseCase {

    @Autowired
    private AlocacaoRepository alocacaoRepository;

    @Autowired
    private DisponibilidadeProfessorRepository disponibilidadeProfessorRepository;

    public void executar(Alocacao entity) {
        var diaSemana = entity.getDiaSemana().getId();
        var horario = entity.getHorario().getId();
        var professorId = entity.getUsuario().getId();

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
                .existsByUsuarioIdAndDiaSemanaIdAndHorarioId(
                        professorId, diaId, horarioId);

        if (temSobreposicao) {
            throw new BusinessException("Professor já está alocado em outra turma/disciplina neste dia e horário.");
        }
    }
}
