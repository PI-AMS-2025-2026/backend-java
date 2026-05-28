package com.fatec.gini.domain.services.usecase.read;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

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
