package com.fatec.horario.dto.curso;

import jakarta.validation.constraints.*;

public class CursoRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String nome;

    @NotBlank
    @Pattern(regexp = "Semestral|Anual")
    private String periodicidade;

    @NotBlank
    @Pattern(regexp = "Ativo|Inativo")
    private String status;

    @NotNull
    @Min(1)
    private Integer duracao;

    public CursoRequest() {}

    public CursoRequest(String nome, String periodicidade, String status, Integer duracao) {
        this.nome = nome;
        this.periodicidade = periodicidade;
        this.status = status;
        this.duracao = duracao;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(String periodicidade) { this.periodicidade = periodicidade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getDuracao() { return duracao; }
    public void setDuracao(Integer duracao) { this.duracao = duracao; }
}