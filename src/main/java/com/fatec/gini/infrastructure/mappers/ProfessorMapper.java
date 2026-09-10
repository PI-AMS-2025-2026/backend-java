package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.dto.professor.ProfessorRequest;
import com.fatec.gini.dto.professor.ProfessorResponse;


public class ProfessorMapper {
    
    public static Professor toEntity(ProfessorRequest request) {
        if (request == null) {
            return null;
        }
        return new Professor(request.nome(),request.email(), request.cidade(), request.status()) ;
    }

    public static ProfessorResponse toResponse(Professor professor) {

        return new ProfessorResponse(
                professor.getId(),
                professor.getNome(),
                professor.getEmail(),
                professor.getCidade(),
                professor.getStatus(),
                professor.getCreatedAt(),
                professor.getUpdatedAt());
    }
}
