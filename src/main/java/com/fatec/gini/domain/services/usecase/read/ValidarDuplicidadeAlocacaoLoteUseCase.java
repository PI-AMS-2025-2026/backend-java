package com.fatec.gini.domain.services.usecase.read;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.dto.alocacao.AlocacaoRequest;
import com.fatec.gini.web.exception.ParameterException;

@Service
public class ValidarDuplicidadeAlocacaoLoteUseCase {
    @Transactional(readOnly = true)
    public void validarDuplicidadesNoLote(List<AlocacaoRequest> requests) {
        Set<String> chavesTurmaHorario = new HashSet<>();
        Set<String> chavesProfessorHorario = new HashSet<>();
        Set<String> chavesSalaHorario = new HashSet<>();

        for (AlocacaoRequest req : requests) {
            if (req.diaSemana() == null || req.horario() == null)
                continue;

            String diaEHorario = req.diaSemana() + "-" + req.horario().id();

            // 1. Evitar mesma turma no mesmo horário no lote
            if (req.turma() != null) {
                String chaveTurma = req.turma().id() + "-" + diaEHorario;
                if (!chavesTurmaHorario.add(chaveTurma)) {
                    throw new ParameterException(
                            "Existem alocações duplicadas para a mesma turma no mesmo horário dentro do lote.");
                }
            }

            // 2. Evitar mesmo professor no mesmo horário no lote
            if (req.professor() != null) {
                String chaveProf = req.professor().id() + "-" + diaEHorario;
                if (!chavesProfessorHorario.add(chaveProf)) {
                    throw new ParameterException(
                            "Existem alocações duplicadas para o mesmo professor no mesmo horário dentro do lote.");
                }
            }

            // 3. Evitar mesma sala no mesmo horário no lote
            if (req.sala() != null) {
                String chaveSala = req.sala().id() + "-" + diaEHorario;
                if (!chavesSalaHorario.add(chaveSala)) {
                    throw new ParameterException(
                            "Existem alocações duplicadas para a mesma sala no mesmo horário dentro do lote.");
                }
            }
        }
    }
}
