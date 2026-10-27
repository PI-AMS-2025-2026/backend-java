package com.fatec.gini.dto.tipoSala;

import java.time.LocalDateTime;

public record TipoSalaResponse(

    Long id,
    String nome,
    LocalDateTime createdAt, // Alteração: inclui a data de criação no payload.
    LocalDateTime updatedAt  // Alteração: inclui a data da última atualização.

) {}