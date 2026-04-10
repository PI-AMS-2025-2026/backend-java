package com.fatec.horario.domain.services.usecase;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.services.usecase.read.ValidarConflitoTurmaHorarioService;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarAlocacaoUseCase {

    private final AlocacaoRepository repository;
    private final ValidarConflitoTurmaHorarioService validarConflitoService;

    public CriarAlocacaoUseCase(
            AlocacaoRepository repository,
            ValidarConflitoTurmaHorarioService validarConflitoService
    ) {
        this.repository = repository;
        this.validarConflitoService = validarConflitoService;
    }

    @Transactional
    public Alocacao executar(Alocacao alocacao) {

        // métodos não existem nas entidades até o momento que estou fazendo a issue
        validarConflitoService.validarTurmaSemConflitoOuLancarExcecao(
                alocacao.getTurma().getId_turma(),
                alocacao.getDiaSemana().getId_dia_semana(),
                alocacao.getHorario().getId_horario()
        );

        return repository.save(alocacao);
    }
}