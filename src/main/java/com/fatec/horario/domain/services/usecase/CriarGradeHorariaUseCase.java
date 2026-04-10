package com.fatec.horario.domain.services.usecase;

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

        // métodos não existem nas entidades até o momento em que estou fazendo a issue
        validarService.validarProfessorPodeLecionarOuLancarExcecao(
                grade.getProfessor().getId_usuario(),
                grade.getDisciplina().getId_disciplina()
        );

        return repository.save(grade);
    }
}