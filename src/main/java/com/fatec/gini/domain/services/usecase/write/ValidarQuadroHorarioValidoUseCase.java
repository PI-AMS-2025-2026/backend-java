package com.fatec.gini.domain.services.usecase.write;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarQuadroHorarioValidoUseCase {

    private final QuadroHorarioRepository quadroHorarioRepository;

    /**
     * Valida se já existe outro quadro horário ativo
     * para o mesmo curso e período atividade quadro.
     */
    public void validarQuadroAtivoDuplicado(
            QuadroHorario quadroHorario) {

        /*
         * A validação só é necessária para quadros ativos.
         */
        if (quadroHorario.getStatus() != Status.ATIVO) {
            return;
        }

        boolean existeConflito;

        /*
         * Criação:
         * como o ID ainda é null, verifica qualquer quadro
         * ativo com o mesmo curso e período.
         */
        if (quadroHorario.getId() == null) {

            existeConflito =
                    quadroHorarioRepository
                            .existsByCursoIdAndPeriodoAtividadeQuadroIdAndStatus(
                                    quadroHorario
                                            .getCurso()
                                            .getId(),

                                    quadroHorario
                                            .getPeriodoAtividadeQuadro()
                                            .getId(),

                                    Status.ATIVO);
        } else {

            /*
             * Atualização:
             * procura outro quadro, ignorando o próprio ID.
             */
            existeConflito =
                    quadroHorarioRepository
                            .existsByCursoIdAndPeriodoAtividadeQuadroIdAndStatusAndIdNot(
                                    quadroHorario
                                            .getCurso()
                                            .getId(),

                                    quadroHorario
                                            .getPeriodoAtividadeQuadro()
                                            .getId(),

                                    Status.ATIVO,

                                    quadroHorario
                                            .getId());
        }

        if (existeConflito) {

            throw new BusinessException(
                    "Já existe um quadro horário ativo para este curso no período atividade quadro selecionado.");
        }
    }

    /**
     * Valida se o quadro horário pode ser alterado.
     *
     * Apenas quadros ativos podem sofrer alterações.
     */
    public void validarAlteracaoGradeAtiva(
            QuadroHorario gradeHorariaAtual) {

        if (gradeHorariaAtual.getStatus() != Status.ATIVO) {

            throw new BusinessException(
                    "Não é permitido realizar alterações em um quadro horário que não esteja ativo.");
        }
    }
}