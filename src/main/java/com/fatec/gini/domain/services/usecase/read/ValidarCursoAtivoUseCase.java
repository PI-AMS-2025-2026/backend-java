package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.web.exception.BusinessException;

@Service
public class ValidarCursoAtivoUseCase {

    /**
     * Valida se o curso está ativo.
     *
     * Cursos inativos não podem ser utilizados
     * para criação, atualização ou reaproveitamento
     * de quadros horários.
     *
     * @param curso curso que será validado
     */
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