package com.fatec.horario.domain.services.usecase.read;

import com.fatec.horario.web.exception.BusinessException;
import com.fatec.horario.infrastructure.repositories.ProfessorDisciplinaRepository;

import org.springframework.stereotype.Service;

@Service
public class ValidarProfessorDisciplinaService {

    private final ProfessorDisciplinaRepository repository;

    public ValidarProfessorDisciplinaService(ProfessorDisciplinaRepository repository) {
        this.repository = repository;
    }

    public void validarProfessorPodeLecionarOuLancarExcecao(
            Long idProfessor,
            Long idDisciplina
    ) {

        boolean vinculado = repository
                .existsByUsuarioIdUsuarioAndDisciplinaIdDisciplina(idProfessor, idDisciplina);

        if (!vinculado) {
            throw new BusinessException(
                "O professor selecionado não está apto a lecionar esta disciplina"
            );
        }
    }
}