package com.fatec.gini.domain.services.usecase.write;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.GradeHoraria;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarGradeHorariaValidaUseCase {

    private final  GradeHorariaRepository gradeHorariaRepository;

    /**
     * Valida se já existe outra grade horária ativa
     * para o mesmo curso e período letivo.
     */
    public void validarGradeAtivaDuplicada(GradeHoraria gradeHoraria) {

        // A validação só é necessária para grades ativas
        if (gradeHoraria.getStatus() != Status.ATIVO) {
            return;
        }

        boolean existeConflito;

        // Criação
        if (gradeHoraria.getId() == null) {

            existeConflito =
                    gradeHorariaRepository
                            .existsByCursoIdAndPeriodoLetivoIdAndStatus(
                                    gradeHoraria.getCurso().getId(),
                                    gradeHoraria.getPeriodoLetivo().getId(),
                                    Status.ATIVO
                            );

        } else {

            // Atualização
            existeConflito =
                    gradeHorariaRepository
                            .existsByCursoIdAndPeriodoLetivoIdAndStatusAndIdNot(
                                    gradeHoraria.getCurso().getId(),
                                    gradeHoraria.getPeriodoLetivo().getId(),
                                    Status.ATIVO,
                                    gradeHoraria.getId()
                            );
        }

        if (existeConflito) {

            throw new BusinessException(
                    "Já existe uma grade horária ativa para este curso no período letivo selecionado."
            );
        }
    }

    /**
     * Valida se a grade horária pode ser alterada.
     * Apenas grades ativas podem sofrer alterações.
     */
    public void validarAlteracaoGradeAtiva(GradeHoraria gradeHorariaAtual) {

        if (gradeHorariaAtual.getStatus() != Status.ATIVO) {

            throw new BusinessException(
                    "Não é permitido realizar alterações em uma grade horária que não esteja ativa."
            );
        }
    }
}