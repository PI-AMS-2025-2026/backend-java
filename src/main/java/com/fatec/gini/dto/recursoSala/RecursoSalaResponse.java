package com.fatec.gini.dto.recursoSala;

import com.fatec.gini.dto.recurso.RecursoResponse;
import com.fatec.gini.dto.sala.SalaResponse;

public record RecursoSalaResponse(
    Long id,
    SalaResponse sala,
    RecursoResponse recurso,
    Integer quantidade
) {}