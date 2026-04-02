package com.fatec.horario.dto.Turma;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TurmaRequest(

        @NotBlank(message = "O código da turma é obrigatório")
        @Size(min = 3, max = 20, message = "O código deve ter entre 3 e 20 caracteres")
        String codigo,

        @NotNull(message = "O período é obrigatório")
        @Positive(message = "O período deve ser um número positivo")
        Integer periodo,

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
