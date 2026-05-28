package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.web.exception.BusinessException;

@Service
public class ValidarCoerenciaUseCase {

    public void validar(Alocacao entity) {

        Long cursoGradeId =
                entity.getGradeHoraria()
                      .getCurso()
                      .getId();

        Long cursoTurmaId =
                entity.getTurma()
                      .getCurso()
                      .getId();

        Long cursoDisciplinaId =
                entity.getDisciplina()
                      .getCurso()
                      .getId();

        // validar turma x grade
        if (!cursoTurmaId.equals(cursoGradeId)) {

            throw new BusinessException(
                    "A turma selecionada não pertence ao curso desta grade");
        }

        // validar disciplina x turma
        if (!cursoDisciplinaId.equals(cursoTurmaId)) {

            throw new BusinessException(
                    "A disciplina não pertence ao curso da turma");
        }
    }
}