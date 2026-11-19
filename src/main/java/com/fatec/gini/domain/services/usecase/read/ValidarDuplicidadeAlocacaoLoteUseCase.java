package com.fatec.gini.domain.services.usecase.read;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.dto.alocacao.AlocacaoRequest;
import com.fatec.gini.web.exception.AlocacaoLoteException;
import com.fatec.gini.web.exception.ParameterException;

@Service
public class ValidarDuplicidadeAlocacaoLoteUseCase {

    @Transactional(readOnly = true)
    public void validarDuplicidadesNoLote(List<AlocacaoRequest> requests) {
        Set<String> chavesTurmaHorario = new HashSet<>();
        Set<String> chavesProfessorHorario = new HashSet<>();
        Set<String> chavesSalaHorario = new HashSet<>();

        for (int indice = 0; indice < requests.size(); indice++) {
            AlocacaoRequest req = requests.get(indice);
            if (req.diaSemana() == null || req.horarioId() == null) {
                continue;
            }

            // Alteração: utiliza diretamente o ID do horário recebido no payload.
            String diaEHorario = req.diaSemana() + "-" + req.horarioId();

            // 1. Evitar mesma turma no mesmo horário no lote
            if (req.turmaId() != null) {
                // Alteração: utiliza diretamente o ID da turma.
                String chaveTurma = req.turmaId() + "-" + diaEHorario;

                if (!chavesTurmaHorario.add(chaveTurma)) {
                    throw new AlocacaoLoteException(indice, new ParameterException(
                            "Existem alocações duplicadas para a mesma turma no mesmo horário dentro do lote."));
                }
            }

            // 2. Evitar mesmo professor no mesmo horário no lote
            if (req.professorId() != null) {
                // Alteração: utiliza diretamente o ID do professor.
                String chaveProf = req.professorId() + "-" + diaEHorario;

                if (!chavesProfessorHorario.add(chaveProf)) {
                    throw new AlocacaoLoteException(indice, new ParameterException(
                            "Existem alocações duplicadas para o mesmo professor no mesmo horário dentro do lote."));
                }
            }

            // 3. Evitar mesma sala no mesmo horário no lote
            if (req.salaId() != null) {
                // Alteração: utiliza diretamente o ID da sala.
                String chaveSala = req.salaId() + "-" + diaEHorario;

                if (!chavesSalaHorario.add(chaveSala)) {
                    throw new AlocacaoLoteException(indice, new ParameterException(
                            "Existem alocações duplicadas para a mesma sala no mesmo horário dentro do lote."));
                }
            }
        }
    }
}
