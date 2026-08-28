package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.web.exception.BusinessException;

@Service
public class ValidarCursoAtivoUseCase {

    public void validar(Curso curso) {

        if (curso == null) {
            throw new BusinessException(
                    "O curso é obrigatório.");
        }

        if (curso.getStatus() != Status.ATIVO) {
            throw new BusinessException(
                    "Não é permitido criar ou alterar quadro horário para um curso inativo.");
        }
    }
}