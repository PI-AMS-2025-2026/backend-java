package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.models.Status;
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

    private final  PeriodoAtividadeQuadroRepository periodoAtividadeQuadroRepository;

    public void executar(QuadroHorario quadroHorario) {
        periodoAtividadeQuadroAtivo(quadroHorario);
    }

    private void periodoAtividadeQuadroAtivo(QuadroHorario quadroHorario) {
        if (!periodoAtividadeQuadroRepository.existsByIdAndStatus(quadroHorario.getPeriodoAtividadeQuadro().getId(),Status.ATIVO)) {
            throw new BusinessException("Não é permitido criar ou alterar quadro horário para um período atividade quadro inativo.");
        }
    }
}