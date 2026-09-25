package com.fatec.gini.dto.tipoRecurso;
import java.time.LocalDateTime;

public record TipoRecursoResponse(

        Long id,
        String nome,
        LocalDateTime created_at, // Alteração: expõe a data de criação no payload.
        LocalDateTime updated_at  // Alteração: expõe a data da última atualização.

) {}
