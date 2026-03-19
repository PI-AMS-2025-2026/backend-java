package com.fatec.horario.dto.Turma;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TurmaRequest(

        @NotBlank(message = "O código da turma é obrigatório")
        String codigo,

        @NotBlank(message = "O período é obrigatório")
        String periodo,

        @NotNull(message = "O ano é obrigatório")
        @Positive(message = "O ano deve ser positivo")
        Integer ano,

        @NotNull(message = "O número de alunos é obrigatório")
        @Positive(message = "O número de alunos deve ser positivo")
        Integer numeroAlunos,

        @NotNull(message = "O id do curso é obrigatório")
        Long idCurso

) {
}
