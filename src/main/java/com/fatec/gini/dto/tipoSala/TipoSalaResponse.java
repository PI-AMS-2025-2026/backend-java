package com.fatec.gini.dto.tipoSala;

import java.time.LocalDateTime;

public record TipoSalaResponse(

    Long id,
    String nome,
    LocalDateTime created_at, // Alteração: inclui a data de criação no payload.
    LocalDateTime updated_at  // Alteração: inclui a data da última atualização.

) {}