package com.fatec.horario.dto.curso;

import com.fatec.horario.domain.entities.Status;

import jakarta.validation.constraints.*;

public record CursoRequest(

    @NotBlank
    @Size(min = 2, max = 100)
    String nome,

    @NotBlank
    @Pattern(regexp = "Semestral|Anual")
    String periodicidade,

    @NotNull
    Status status,

    @NotNull
    @Min(1)
    Integer duracao
) {
}