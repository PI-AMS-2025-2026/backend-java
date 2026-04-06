package com.fatec.horario.dto.curso;

import com.fatec.horario.domain.entities.Status;

import jakarta.validation.constraints.*;

public record CursoRequest(

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    String nome,

    @NotBlank(message = "Periodicidade é obrigatória")
    @Pattern(regexp = "Semestral|Anual", message = "Periodicidade deve ser 'Semestral' ou 'Anual'")
    String periodicidade,

    @NotNull(message = "Status é obrigatório")
    Status status,

    @NotNull(message = "Duração é obrigatória")
    @Min(value = 1, message = "Duração deve ser maior ou igual a 1")
    Integer duracao
) {
}