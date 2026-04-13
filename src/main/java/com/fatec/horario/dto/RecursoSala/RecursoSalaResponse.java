package com.fatec.horario.dto.RecursoSala;

public record RecursoSalaResponse(
    Long id,
    Long salaId,
    Long recursoId,
    Integer quantidade
) {}