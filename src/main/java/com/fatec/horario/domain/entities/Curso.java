package com.fatec.horario.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_curso;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String periodicidade;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer duracao;

    // Construtor vazio
    public Curso() {}

    // Construtor com campos
    public Curso(Long id_curso, String nome, String periodicidade, String status, Integer duracao) {
        this.id_curso = id_curso;
        this.nome = nome;
        this.periodicidade = periodicidade;
        this.status = status;
        this.duracao = duracao;
    }

    // Getters e Setters
    public Long getId_curso() {
        return id_curso;
    }

    public void setId_curso(Long id_curso) {
        this.id_curso = id_curso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(String periodicidade) {
        this.periodicidade = periodicidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }
}