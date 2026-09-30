package com.fatec.gini.domain.services.usecase.read;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.fatec.gini.dto.blocoHorario.BlocoHorarioKey;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioRequest;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.web.exception.ParameterException;

import lombok.RequiredArgsConstructor;

/**
 * Valida se:
 * <li>Hora de fim é posterior a hora inicio</li>
 * <li>Valida se já existe horario com mesmo inicio e fim</li>
 */
@Service
@RequiredArgsConstructor
public class ValidarBlocoHorarioUseCase {

    private final BlocoHorarioRepository repository;

    public void executar(BlocoHorarioRequest request) {
        if (request.horaFim().isBefore(request.horaInicio())) {
            throw new ParameterException(
                    "Hora de fim deve ser posterior à hora de início");
        }

        if (repository.existsByHoraInicioAndHoraFim(
                request.horaInicio(),
                request.horaFim())) {
            throw new ParameterException(
                    "Já existe um bloco horário com esse intervalo: " + request.horaInicio() + "-" + request.horaFim());
        }
    }

    public void validarDuplicidadesNoLote(
            List<BlocoHorarioRequest> requests) {
        Set<BlocoHorarioKey> horarios = new HashSet<>();

        for (BlocoHorarioRequest request : requests) {
            BlocoHorarioKey key = new BlocoHorarioKey(
                    request.horaInicio(),
                    request.horaFim());

            if (!horarios.add(key)) {
                throw new ParameterException(
                        "Existem blocos horários duplicados no lote");
            }
        }
    }
}