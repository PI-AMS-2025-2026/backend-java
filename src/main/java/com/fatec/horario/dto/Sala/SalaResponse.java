package com.fatec.horario.dto.sala;

public record SalaResponse(
        Long id,
        String codigo,
        Integer capacidade,
        Long idTipoSala
) {}