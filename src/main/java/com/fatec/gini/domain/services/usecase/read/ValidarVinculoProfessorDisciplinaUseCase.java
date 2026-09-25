package com.fatec.gini.domain.services.usecase.read;


import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.infrastructure.repositories.ProfessorDisciplinaRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * Valida:
 * </p>
 * <ul>
 * <li>professor está vinculado à disciplina</li>
 * <li>professor pode ministrar aquela disciplina</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ValidarVinculoProfessorDisciplinaUseCase {

    private final  ProfessorDisciplinaRepository repository;

    public void executar(Alocacao entity) {
        Long idProfessor = entity.getProfessor().getId();
        Long idDisciplina = entity.getDisciplina().getId();

        professorPodeLecionarOuLancarExcecao(idProfessor, idDisciplina);

    }

    private void professorPodeLecionarOuLancarExcecao(Long idProfessor, Long idDisciplina) {

        if (!repository.existsByProfessorIdAndDisciplinaId(idProfessor, idDisciplina)) {
            throw new BusinessException(
                    "O professor selecionado não está apto a lecionar esta disciplina");
        }
    }

}
