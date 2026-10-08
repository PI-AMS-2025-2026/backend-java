package com.fatec.gini.dto.curso;

import com.fatec.gini.domain.models.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CursoRequest(

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    String nome,

    @NotBlank(message = "Periodicidade é obrigatória")
    String periodicidade,

    @NotNull(message = "Status é obrigatório")
    Status status,

    @NotNull(message = "Duração é obrigatória")
    Integer duracao

) {
}