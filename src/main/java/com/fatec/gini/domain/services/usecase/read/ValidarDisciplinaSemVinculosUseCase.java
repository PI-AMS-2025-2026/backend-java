package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorDisciplinaRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarDisciplinaSemVinculosUseCase {

    private final  AlocacaoRepository alocacaoRepository;

    private final  ProfessorDisciplinaRepository professorDisciplinaRepository;

    @Transactional(readOnly = true)
    public void validar(Long idDisciplina) {
        if (alocacaoRepository.existsByDisciplinaId(idDisciplina)) {
            throw new BusinessException("Não é possível excluir disciplina vinculada a alocações.");
        }

        if (professorDisciplinaRepository.existsByDisciplinaId(idDisciplina)) {
            throw new BusinessException("Não é possível excluir disciplina vinculada a professores.");
        }
    }
}
