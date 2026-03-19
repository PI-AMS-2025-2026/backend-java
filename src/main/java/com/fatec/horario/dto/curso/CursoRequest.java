package com.fatec.horario.dto.curso;

import jakarta.validation.constraints.*;

public record CursoRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Periodicidade é obrigatória")
        @Pattern(regexp = "Semestral|Anual", message = "Periodicidade deve ser Semestral ou Anual")
        String periodicidade,

        @NotBlank(message = "Status é obrigatório")
        String status,

        @NotNull(message = "Duração é obrigatória")
        @Min(value = 1, message = "Duração deve ser maior que 0")
        Integer duracao

) {}