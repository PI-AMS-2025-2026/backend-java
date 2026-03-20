package com.fatec.horario.dto.DiaSemana;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DiaSemanaRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 20, message = "Nome deve ter entre 3 e 20 caracteres")
    private String nome;
}