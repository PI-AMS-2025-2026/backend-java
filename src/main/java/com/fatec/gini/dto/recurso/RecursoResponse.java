package com.fatec.gini.dto.recurso;

import com.fatec.gini.dto.tipoRecurso.TipoRecursoResponse;

public record RecursoResponse(
    Long id,
    String nome,
    TipoRecursoResponse tipo
) {
}

