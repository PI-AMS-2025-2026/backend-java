package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarCoerenciaUseCase {

    public void validar(Alocacao entity) {

        Long cursoQuadroId =
                entity.getQuadroHorario()
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

        // validar turma x quadro
        if (!cursoTurmaId.equals(cursoQuadroId)) {

            throw new BusinessException(
                    "A turma selecionada não pertence ao curso deste quadro");
        }

        // validar disciplina x turma
        if (!cursoDisciplinaId.equals(cursoTurmaId)) {

            throw new BusinessException(
                    "A disciplina não pertence ao curso da turma");
        }
    }
}