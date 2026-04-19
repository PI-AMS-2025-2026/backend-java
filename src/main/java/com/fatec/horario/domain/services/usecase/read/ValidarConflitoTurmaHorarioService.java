package com.fatec.horario.domain.services.usecase.read;

import com.fatec.horario.web.exception.BusinessException;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;

import org.springframework.stereotype.Service;

@Service
public class ValidarConflitoTurmaHorarioService {

    private final AlocacaoRepository repository;

    public ValidarConflitoTurmaHorarioService(AlocacaoRepository repository) {
        this.repository = repository;
    }

    public void validarTurmaSemConflitoOuLancarExcecao(
            Long idTurma,
            Long idDiaSemana,
            Long idHorario
    ) {

        boolean existeConflito = repository.existsConflitoTurmaHorario(
                idTurma,
                idDiaSemana,
                idHorario
        );

        if (existeConflito) {
            throw new BusinessException(
                "A turma já possui uma disciplina alocada neste dia e horário"
            );
        }
    }
}