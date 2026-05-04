package com.fatec.horario.domain.services.usecase.read;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.Status;
import com.fatec.horario.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.horario.web.exception.BusinessException;

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
public class ValidarGradeHorariaUseCase {

    @Autowired
    private GradeHorariaRepository gradeHorariaRepository;

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
