package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.infrastructure.repositories.PeriodoLetivoRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * Responsável por garantir que o período letivo da quadro horário está ativo.
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

    public void executar(QuadroHorario quadroHorario) {
        periodoLetivoAtivo(quadroHorario);
    }

    private void periodoLetivoAtivo(QuadroHorario quadroHorario) {
        if (!periodoLetivoRepository.existsByIdAndStatus(quadroHorario.getPeriodoLetivo().getId(),Status.ATIVO)) {
            throw new BusinessException("Não é permitido criar ou alterar quadro horário para um período letivo inativo.");
        }
    }
}