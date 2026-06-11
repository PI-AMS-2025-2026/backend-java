package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.GradeHoraria;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.infrastructure.repositories.PeriodoLetivoRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * Responsável por garantir que o período letivo da grade horária está ativo.
 *
 * <p>
 * Valida:
 * </p>
 * <li>período letivo está ativo</li>
 */
@Service
@RequiredArgsConstructor
public class ValidarPeriodoLetivoAtivoUseCase {

    private final  PeriodoLetivoRepository periodoLetivoRepository;

    public void executar(GradeHoraria gradeHoraria) {
        periodoLetivoAtivo(gradeHoraria);
    }

    private void periodoLetivoAtivo(GradeHoraria gradeHoraria) {
        if (!periodoLetivoRepository.existsByIdAndStatus(gradeHoraria.getPeriodoLetivo().getId(),Status.ATIVO)) {
            throw new BusinessException("Não é permitido criar ou alterar grade horária para um período letivo inativo.");
        }
    }
}