package com.fatec.horario.dto.curso;

import jakarta.validation.constraints.*;

public record CursoRequest(

    @NotBlank
    @Size(min = 2, max = 100)
    String nome,

    @NotBlank
    @Pattern(regexp = "Semestral|Anual")
    String periodicidade,

    @NotBlank
    @Pattern(regexp = "Ativo|Inativo")
    String status,

    @NotNull
    @Min(1)
    Integer duracao
) {
}