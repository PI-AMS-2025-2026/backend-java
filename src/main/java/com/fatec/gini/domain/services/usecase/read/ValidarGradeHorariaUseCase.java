package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.GradeHoraria;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * Responsável por garantir que a grade da alocação é válida.
 * 
 * <p>
 * Valida:
 * </p>
 * <li>grade existe</li>
 * <li>grade está ativa</li>
 * <li>grade é a última versão</li>
 * 
 */
@Service
@RequiredArgsConstructor
public class ValidarGradeHorariaUseCase {

    private final  GradeHorariaRepository gradeHorariaRepository;

    public void executar(Alocacao entity) {
        var gradeHoraria = entity.getGradeHoraria();
        gradeHorariaAtiva(gradeHoraria);
        gradeHorariaUltimaVersao(gradeHoraria);

    }

    private void gradeHorariaAtiva(GradeHoraria gradeHoraria) {
        if (gradeHoraria.getStatus() != Status.ATIVO) {
            throw new BusinessException("A grade horária informada deve estar ativa.");
        }
    }

    private void gradeHorariaUltimaVersao(GradeHoraria gradeHoraria) {
        GradeHoraria ultimaGradeHoraria = gradeHorariaRepository
                .findTopByCursoIdAndPeriodoLetivoIdOrderByVersaoDesc(
                        gradeHoraria.getCurso().getId(),
                        gradeHoraria.getPeriodoLetivo().getId())
                .orElseThrow(() -> new BusinessException("Não foi possível validar a última versão da grade horária."));

        if (!gradeHoraria.getId().equals(ultimaGradeHoraria.getId())) {
            throw new BusinessException("A grade horária informada não é a última versão disponível.");
        }
    }

}
