package com.fatec.gini.dto.sala;

import java.time.LocalDateTime;

import com.fatec.gini.dto.tipoSala.TipoSalaResponse;

public record SalaResponse(
                Long id,
                String codigo,
                Integer capacidade,
                TipoSalaResponse tipoSala,
                LocalDateTime created_at,
                LocalDateTime updated_at) {
}