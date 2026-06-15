package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.infrastructure.repositories.PeriodoAtividadeQuadroRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * Responsável por garantir que o período atividade quadro da quadro horário está ativo.
 *
 * <p>
 * Valida:
 * </p>
 * <li>período atividade quadro está ativo</li>
 */
@Service
@RequiredArgsConstructor
public class ValidarPeriodoAtividadeQuadroAtivoUseCase {

    private final  PeriodoAtividadeQuadroRepository periodoLetivoRepository;

    public void executar(QuadroHorario quadroHorario) {
        periodoLetivoAtivo(quadroHorario);
    }

    private void periodoLetivoAtivo(QuadroHorario quadroHorario) {
        if (!periodoLetivoRepository.existsByIdAndStatus(quadroHorario.getPeriodoAtividadeQuadro().getId(),Status.ATIVO)) {
            throw new BusinessException("Não é permitido criar ou alterar quadro horário para um período atividade quadro inativo.");
        }
    }
}