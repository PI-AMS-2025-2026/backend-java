package com.fatec.gini.dto.professor;

import java.time.LocalDateTime;

import com.fatec.gini.domain.models.Status;

public record ProfessorResponse(
        Long id,
        String nome,
        String email,
        String cidade,
        Status status,
        // Padroniza os nomes dos campos do payload em camelCase.
        LocalDateTime createdAt,
        // Padroniza os nomes dos campos do payload em camelCase.
        LocalDateTime updatedAt) {

}
