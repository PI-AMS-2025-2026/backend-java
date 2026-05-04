package com.fatec.horario.domain.services.usecase.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.web.exception.BusinessException;

@Service
public class ValidarTurmaSemVinculosUseCase {

    @Autowired
    private AlocacaoRepository alocacaoRepository;

    @Transactional(readOnly = true)
    public void validar(Long idTurma) {
        if (alocacaoRepository.existsByTurmaId(idTurma)) {
            throw new BusinessException("Não é possível excluir turma vinculada a alocações.");
        }
    }
}
