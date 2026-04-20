package com.fatec.horario.dto.recursoSala;

import com.fatec.horario.dto.recurso.RecursoResponse;
import com.fatec.horario.dto.sala.SalaResponse;

public record RecursoSalaResponse(
    Long id,
    SalaResponse sala,
    RecursoResponse recurso,
    Integer quantidade
) {}