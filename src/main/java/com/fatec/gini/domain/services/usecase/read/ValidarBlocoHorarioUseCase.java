package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.dto.blocoHorario.BlocoHorarioRequest;
import com.fatec.gini.web.exception.ParameterException;

/**
 * Valida se Hora de fim é posterior a hora inicio
 */
@Service
public class ValidarBlocoHorarioUseCase {
    public void executar(BlocoHorarioRequest request) {
        if (request.horaFim().isBefore(request.horaInicio())) {
            throw new ParameterException("Hora de fim deve ser posterior à hora de início");
        }
    }
}
