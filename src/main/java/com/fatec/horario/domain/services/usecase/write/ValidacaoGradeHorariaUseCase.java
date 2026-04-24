package com.fatec.horario.domain.services.usecase.write;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.Status;
import com.fatec.horario.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.horario.web.exception.BusinessException;

@Service
public class ValidacaoGradeHorariaUseCase {

    @Autowired
    private GradeHorariaRepository gradeHorariaRepository;

    public void validarGradeHorariaAtivaEUltimaVersao(GradeHoraria gradeHoraria) {
        if (gradeHoraria == null) {
            throw new BusinessException("Grade horária é obrigatória.");
        }

        if (gradeHoraria.getStatus() != Status.ATIVO) {
            throw new BusinessException("A grade horária informada deve estar ativa.");
        }

        GradeHoraria ultimaGradeHoraria = gradeHorariaRepository
                .findTopByCursoIdAndPeriodoLetivoIdOrderByVersaoDesc(
                        gradeHoraria.getCurso().getId(),
                        gradeHoraria.getPeriodoLetivo().getId())
                .orElseThrow(() -> new BusinessException("Não foi possível validar a última versão da grade horária."));

        if (!Objects.equals(gradeHoraria.getId(), ultimaGradeHoraria.getId())) {
            throw new BusinessException("A grade horária informada não é a última versão disponível.");
        }
    }
}