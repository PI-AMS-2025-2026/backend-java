package com.fatec.horario.domain.services.usecase.read;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.web.exception.BusinessException;

/**
 * <p>
 * Valida:
 * </p>
 * <ul>
 * <li>a mesma turma não pode ter duas disciplinas no mesmo dia e horário</li>
 * <li>não pode haver sobreposição de aula para a turma</li>
 * </ul>
 */
@Service
public class ValidarConflitoTurmaHorarioUseCase {
    @Autowired
    private AlocacaoRepository repository;

    public void executar(Alocacao entity) {
        boolean conflito = repository.existsConflitoTurmaHorario(
                entity.getTurma().getId(),
                entity.getDiaSemana().getId(),
                entity.getHorario().getId());

        if (conflito) {
            throw new BusinessException("A turma já possui uma alocação neste dia/horário");
        }
    }

}
