package com.fatec.horario.dto.sala;

import com.fatec.horario.dto.tipoSala.TipoSalaResponse;

public record SalaResponse(
        Long id,
        String codigo,
        Integer capacidade,
        TipoSalaResponse tipoSala
) {}