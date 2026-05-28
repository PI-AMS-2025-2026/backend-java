package com.fatec.gini.domain.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String periodicidade;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Integer duracao;

    // Data de criação
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Data de atualização
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "curso")
    private List<Turma> turmas;

    @OneToMany(mappedBy = "curso")
    private List<Disciplina> disciplinas;

    @OneToMany(mappedBy = "curso")
    private List<GradeHoraria> gradeHorarias;

    @OneToMany(mappedBy = "curso")
    private List<Usuario> usuarios;

    public Curso() {
    }

    public Curso(String nome, String periodicidade, Status status, Integer duracao) {
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

    public Status getStatus() {
        return status;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Turma> getTurmas() {
        return turmas;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public List<GradeHoraria> getGradeHorarias() {
        return gradeHorarias;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPeriodicidade(String periodicidade) {
        this.periodicidade = periodicidade;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTurmas(List<Turma> turmas) {
        this.turmas = turmas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }

    public void setGradeHorarias(List<GradeHoraria> gradeHorarias) {
        this.gradeHorarias = gradeHorarias;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}