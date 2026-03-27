package com.fatec.horario.dto.curso;

public class CursoResponse {

    private Long id;
    private String nome;
    private String periodicidade;
    private String status;
    private Integer duracao;

    public CursoResponse() {}

    public CursoResponse(Long id, String nome, String periodicidade, String status, Integer duracao) {
        this.id = id;
        this.nome = nome;
        this.periodicidade = periodicidade;
        this.status = status;
        this.duracao = duracao;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getPeriodicidade() {
        return periodicidade;
    }

    public String getStatus() {
        return status;
    }

    public Integer getDuracao() {
        return duracao;
    }
}