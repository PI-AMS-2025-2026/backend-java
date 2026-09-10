package com.fatec.gini.dto.professor;

import com.fatec.gini.domain.models.Status;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfessorRequest(
       @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Email(message = "E-mail deve ter um formato válido")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        String cidade,

        @NotNull(message = "Status é obrigatório")
        Status status

) {

}
