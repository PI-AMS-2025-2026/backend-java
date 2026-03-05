package com.fatec.horario.dto.ClassGroup;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ClassGroupRequest(

        // Quantidade de alunos na turma
        @NotNull(message = "Student Count is required")
        @Positive(message = "Student Count must be positive")
        Integer studentCount,

        // ID do curso ao qual a turma pertence
        @NotNull(message = "Course id is required")
        Long courseId

) {

}
