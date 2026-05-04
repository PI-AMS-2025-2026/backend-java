package com.fatec.horario.domain.services.usecase.read;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.infrastructure.repositories.ProfessorDisciplinaRepository;
import com.fatec.horario.web.exception.BusinessException;

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
public class ValidarVinculoProfessorDisciplinaUseCase {

    @Autowired
    private ProfessorDisciplinaRepository repository;

    public void executar(Alocacao entity) {
        Long idProfessor = entity.getUsuario().getId();
        Long idDisciplina = entity.getDisciplina().getId();

        professorPodeLecionarOuLancarExcecao(idProfessor, idDisciplina);

    }

    private void professorPodeLecionarOuLancarExcecao(Long idProfessor, Long idDisciplina) {

        if (!repository.existsByUsuarioIdAndDisciplinaId(idProfessor, idDisciplina)) {
            throw new BusinessException(
                    "O professor selecionado não está apto a lecionar esta disciplina");
        }
    }

}
