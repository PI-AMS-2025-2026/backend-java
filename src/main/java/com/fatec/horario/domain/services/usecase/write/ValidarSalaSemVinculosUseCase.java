package com.fatec.horario.domain.services.usecase.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.web.exception.BusinessException;

@Service
public class ValidarSalaSemVinculosUseCase {

    @Autowired
    private AlocacaoRepository alocacaoRepository;

    @Transactional(readOnly = true)
    public void validar(Long idSala) {
        if (alocacaoRepository.existsBySalaId(idSala)) {
            throw new BusinessException("Não é possível excluir sala vinculada a alocações.");
        }
    }
}
