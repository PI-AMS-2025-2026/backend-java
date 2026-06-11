package com.fatec.gini.domain.services.usecase.write;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarQuadroHorarioValidoUseCase {

    private final  QuadroHorarioRepository quadroHorarioRepository;

    /**
     * Valida se já existe outra grade horária ativa
     * para o mesmo curso e período letivo.
     */
    public void validarQuadroAtivoDuplicado(QuadroHorario quadroHorario) {

        // A validação só é necessária para grades ativas
        if (quadroHorario.getStatus() != Status.ATIVO) {
            return;
        }

        boolean existeConflito;

        // Criação
        if (quadroHorario.getId() == null) {

            existeConflito =
                    quadroHorarioRepository
                            .existsByCursoIdAndPeriodoLetivoIdAndStatus(
                                    quadroHorario.getCurso().getId(),
                                    quadroHorario.getPeriodoLetivo().getId(),
                                    Status.ATIVO
                            );

        } else {

            // Atualização
            existeConflito =
                    quadroHorarioRepository
                            .existsByCursoIdAndPeriodoLetivoIdAndStatusAndIdNot(
                                    quadroHorario.getCurso().getId(),
                                    quadroHorario.getPeriodoLetivo().getId(),
                                    Status.ATIVO,
                                    quadroHorario.getId()
                            );
        }

        if (existeConflito) {

            throw new BusinessException(
                    "Já existe um quadro horário ativa para este curso no período letivo selecionado."
            );
        }
    }

    /**
     * Valida se a quadro horário pode ser alterada.
     * Apenas quadro ativos podem sofrer alterações.
     */
    public void validarAlteracaoGradeAtiva(QuadroHorario gradeHorariaAtual) {

        if (gradeHorariaAtual.getStatus() != Status.ATIVO) {

            throw new BusinessException(
                    "Não é permitido realizar alterações em um quadro horário que não esteja ativa."
            );
        }
    }
}