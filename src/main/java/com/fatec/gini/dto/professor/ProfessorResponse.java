package com.fatec.gini.dto.professor;

import java.time.LocalDateTime;

import com.fatec.gini.domain.models.Status;

public record ProfessorResponse(
        Long id,
        String nome,
        String email,
        String cidade,
        Status status,
        LocalDateTime created_at,
        LocalDateTime updated_at) {

}
