package com.fatec.horario.dto;

public class ProfessorDisciplinaResponse {

    private Long id;
    private Long usuarioId;
    private Long disciplinaId;

    public ProfessorDisciplinaResponse(Long id, Long usuarioId, Long disciplinaId) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.disciplinaId = disciplinaId;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getDisciplinaId() {
        return disciplinaId;
    }
}
