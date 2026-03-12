package com.fatec.horario.dto.curso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CursoRequisicao(

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 255, message = "O nome deve ter entre 2 e 255 caracteres")
    String nome,

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 2, max = 255, message = "A descrição deve ter entre 2 e 255 caracteres")
    String descricao,

    Long modalidadeId,
    Long periodicidadeId

) {
}