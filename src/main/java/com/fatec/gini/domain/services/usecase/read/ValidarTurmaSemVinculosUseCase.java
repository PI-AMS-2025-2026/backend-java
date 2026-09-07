package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarTurmaSemVinculosUseCase {

    private final AlocacaoRepository alocacaoRepository;

    @Transactional(readOnly = true)
    public void validar(Long idTurma) {

        if (alocacaoRepository.existsByTurmaId(idTurma)) {

            throw new BusinessException(
                    "Não é possível excluir turma vinculada a alocações.");
        }
    }
}