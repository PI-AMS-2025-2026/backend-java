package com.fatec.gini.dto.recurso;

import java.time.LocalDateTime;

import com.fatec.gini.dto.tipoRecurso.TipoRecursoResponse;

public record RecursoResponse(
        Long id,
        String nome,
        TipoRecursoResponse tipo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

}
