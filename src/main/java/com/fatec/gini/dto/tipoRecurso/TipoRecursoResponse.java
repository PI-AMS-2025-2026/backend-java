package com.fatec.gini.dto.tipoRecurso;
import java.time.LocalDateTime;

public record TipoRecursoResponse(

        Long id,
        String nome,
        LocalDateTime createdAt, // Alteração: expõe a data de criação no payload.
        LocalDateTime updatedAt  // Alteração: expõe a data da última atualização.

) {}
