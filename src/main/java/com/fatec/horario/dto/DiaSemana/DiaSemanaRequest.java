package com.fatec.horario.dto.diaSemana;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DiaSemanaRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 20, message = "Nome deve ter entre 3 e 20 caracteres")
    private String nome;

    public DiaSemanaRequest() {
    }

    public DiaSemanaRequest(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}