package com.fatec.horario.dto;

import jakarta.validation.constraints.NotNull;

public class ProfessorDisciplinaRequest {

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long disciplinaId;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getDisciplinaId() {
        return disciplinaId;
    }
}

