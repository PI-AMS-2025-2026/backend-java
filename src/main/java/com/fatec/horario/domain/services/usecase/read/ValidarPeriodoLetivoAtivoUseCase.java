package com.fatec.horario.domain.services.usecase.read;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.infrastructure.repositories.PeriodoLetivoRepository;
import com.fatec.horario.web.exception.BusinessException;

/**
 * Responsável por garantir que o período letivo da grade horária está ativo.
 *
 * <p>
 * Valida:
 * </p>
 * <li>período letivo está ativo</li>
 */
@Service
public class ValidarPeriodoLetivoAtivoUseCase {

    @Autowired
    private PeriodoLetivoRepository periodoLetivoRepository;

    public void executar(GradeHoraria gradeHoraria) {
        periodoLetivoAtivo(gradeHoraria);
    }

    private void periodoLetivoAtivo(GradeHoraria gradeHoraria) {
        if (!periodoLetivoRepository.existsPeriodoLetivoAtivo(gradeHoraria.getPeriodoLetivo().getId())) {
            throw new BusinessException("Não é permitido criar ou alterar grade horária para um período letivo inativo.");
        }
    }
}