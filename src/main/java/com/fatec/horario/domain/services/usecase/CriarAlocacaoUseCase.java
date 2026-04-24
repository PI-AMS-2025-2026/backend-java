package com.fatec.horario.domain.services.usecase;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.services.usecase.read.ValidarConflitoTurmaHorarioService;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarAlocacaoUseCase {

    private final AlocacaoRepository repository;
    private final ValidarConflitoTurmaHorarioService validarService;

    public CriarAlocacaoUseCase(
            AlocacaoRepository repository,
            ValidarConflitoTurmaHorarioService validarService
    ) {
        this.repository = repository;
        this.validarService = validarService;
    }

    @Transactional
    public Alocacao executar(Alocacao alocacao) {

        validarService.validarTurmaSemConflitoOuLancarExcecao(
                alocacao.getTurma().getId(),
                alocacao.getDiaSemana().getId(),
                alocacao.getHorario().getId()
        );

        return repository.save(alocacao);
    }
}