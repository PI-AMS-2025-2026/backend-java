package com.fatec.gini.dto.alocacao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AlocacaoRequest(

    @NotNull(message = "Turma é obrigatória")
    @Valid
    LongDTO turma,

    @NotNull(message = "Disciplina é obrigatória")
    @Valid
    LongDTO disciplina,

    @NotNull(message = "Sala é obrigatória")
    @Valid
    LongDTO sala,

    @NotNull(message = "Professor é obrigatório")
    @Valid
    LongDTO professor,

    @NotNull(message = "Dia da semana é obrigatório")
    DiaSemana diaSemana,

    @NotNull(message = "Bloco de horário é obrigatório")
    @Valid
    LongDTO blocoHorario,

    @NotNull(message = "Quadro horário é obrigatório")
    @Valid
    LongDTO quadroHorario,

    String justificativaAlteracao,

    @NotNull(message = "Usuário responsável pela alteração é obrigatório")
    @Valid
    LongDTO usuarioAlteracao
) {

    public AlocacaoRequest(
            Long turmaId,
            Long disciplinaId,
            Long salaId,
            Long professorId,
            DiaSemana diaSemana,
            Long horarioId,
            Long quadroHorarioId,
            String justificativaAlteracao,
            Long usuarioAlteracaoId) {
        this(
                turmaId != null ? new LongDTO(turmaId) : null,
                disciplinaId != null ? new LongDTO(disciplinaId) : null,
                salaId != null ? new LongDTO(salaId) : null,
                professorId != null ? new LongDTO(professorId) : null,
                diaSemana,
                horarioId != null ? new LongDTO(horarioId) : null,
                quadroHorarioId != null ? new LongDTO(quadroHorarioId) : null,
                justificativaAlteracao,
                usuarioAlteracaoId != null ? new LongDTO(usuarioAlteracaoId) : null
        );
    }

    @JsonIgnore
    public Long turmaId() {
        return turma != null ? turma.id() : null;
    }

    @JsonIgnore
    public Long disciplinaId() {
        return disciplina != null ? disciplina.id() : null;
    }

    @JsonIgnore
    public Long salaId() {
        return sala != null ? sala.id() : null;
    }

    @JsonIgnore
    public Long professorId() {
        return professor != null ? professor.id() : null;
    }

    @JsonIgnore
    public Long horarioId() {
        return blocoHorario != null ? blocoHorario.id() : null;
    }

    @JsonIgnore
    public Long quadroHorarioId() {
        return quadroHorario != null ? quadroHorario.id() : null;
    }

    @JsonIgnore
    public Long usuarioAlteracaoId() {
        return usuarioAlteracao != null ? usuarioAlteracao.id() : null;
    }
}