package com.fatec.horario.dto.diaSemana;

public class DiaSemanaResponse {
    private Long id;
    private String nome;

    public DiaSemanaResponse() {
    }

    public DiaSemanaResponse(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}