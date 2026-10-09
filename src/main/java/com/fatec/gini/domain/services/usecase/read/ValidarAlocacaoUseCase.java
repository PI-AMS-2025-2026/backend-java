package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;

import lombok.RequiredArgsConstructor;

/**
 * Centraliza todas as validações necessárias para uma alocação.
 *
 * Este UseCase é utilizado tanto na criação quanto na
 * sugestão automática.
 *
 * Dessa forma, todos os fluxos utilizam exatamente as mesmas regras
 * de negócio, evitando divergências entre criação e sugestão.
 */
@Service
@RequiredArgsConstructor
public class ValidarAlocacaoUseCase {

    private final ValidarReferenciasObrigatoriasAlocacaoUseCase validarRefObrigatorias;

    private final ValidarDisciplinaTipoSalaUseCase validarDisciplinaTipoSala;

    private final ValidarCargaHorariaDisciplinaUseCase validarCargaHorariaDisciplina;

    private final ValidarCargaHorariaMaximaProfessorUseCase validarCargaHorariaProfessor;

    private final ValidarQuadroHorarioUseCase validarQuadroHorario;

    private final ValidarVinculoProfessorDisciplinaUseCase validarVincProfDisciplina;

    private final ValidarDisponibilidadeProfessorUseCase validarDisponibilidadeProfessor;

    private final ValidarConflitoTurmaBlocoHorarioUseCase validarConflitoTurmaHorario;

    private final ValidarConflitoSalaBlocoHorarioUseCase validarConflitoSalaHorario;

    private final ValidarCapacidadeSalaUseCase validarCapacidadeSala;

    private final ValidarDuplicidadeAlocacaoUseCase validarDuplicidade;

    private final ValidarCoerenciaUseCase validarCoerenciaCurso;

    /**
     * Executa todas as validações necessárias para uma alocação.
     *
     * @param entity alocação que será validada
     */
    public void executar(Alocacao entity) {

        validarRefObrigatorias.validar(entity);

        validarDisciplinaTipoSala.validarCompatibilidadeDisciplinaSala(
                entity.getDisciplina(),
                entity.getSala()
        );

        validarCargaHorariaDisciplina.executar(entity);

        validarCargaHorariaProfessor.validar(
                entity.getProfessor().getId(),
                entity.getDiaSemana()
        );

        validarQuadroHorario.executar(entity);

        validarVincProfDisciplina.executar(entity);

        validarDisponibilidadeProfessor.executar(entity);

        validarConflitoTurmaHorario.executar(entity);

        validarConflitoSalaHorario.executar(entity);

        validarCapacidadeSala.executar(entity);

        validarDuplicidade.executarCriacao(entity);

        validarCoerenciaCurso.validar(entity);
    }
}