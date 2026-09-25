package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public class ValidarConflitoTurmaBlocoHorarioUseCase {
    private final  AlocacaoRepository repository;

    public void executar(Alocacao entity) {
        boolean conflito = repository.existsConflitoTurmaHorario(
                entity.getTurma().getId(),
                entity.getDiaSemana(),
                entity.getBlocoHorario().getId());

        if (conflito) {
            throw new BusinessException("A turma já possui uma alocação neste dia/horário");
        }
    }

}
