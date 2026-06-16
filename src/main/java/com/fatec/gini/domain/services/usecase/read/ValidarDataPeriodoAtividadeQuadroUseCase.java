package com.fatec.gini.domain.services.usecase.read;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.fatec.gini.web.exception.ParameterException;
/**
 * Valida se data de fim é posterior a data de inicio
 */
@Service
public class ValidarDataPeriodoAtividadeQuadroUseCase {

    public void executar(LocalDate dataInicio, LocalDate dataFim) {
        if (dataFim.isAfter(dataInicio)) {
            throw new ParameterException("Data de fim deve ser posterior à data de início");
        }
    }
}
