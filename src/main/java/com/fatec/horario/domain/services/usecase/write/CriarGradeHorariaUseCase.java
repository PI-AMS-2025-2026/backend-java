package com.fatec.horario.domain.services.usecase.write;

import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.services.usecase.read.ValidarProfessorDisciplinaService;
import com.fatec.horario.infrastructure.repositories.GradeHorariaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarGradeHorariaUseCase {

    private final GradeHorariaRepository repository;
    private final ValidarProfessorDisciplinaService validarService;

    public CriarGradeHorariaUseCase(
            GradeHorariaRepository repository,
            ValidarProfessorDisciplinaService validarService
    ) {
        this.repository = repository;
        this.validarService = validarService;
    }

    @Transactional
    public GradeHoraria executar(GradeHoraria grade) {

        validarService.validarProfessorPodeLecionarOuLancarExcecao(
                grade.getProfessor().getId(),
                grade.getDisciplina().getId()
        );

        return repository.save(grade);
    }
}