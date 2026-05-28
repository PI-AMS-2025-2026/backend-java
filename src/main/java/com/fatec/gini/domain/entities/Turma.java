package com.fatec.gini.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "turma")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turma")
    private Long id;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private Integer periodo;

    @Column(nullable = false)
    private Integer ano;

    @Column(name = "numero_alunos", nullable = false)
    private Integer numeroAlunos;

    // Data de criação do registro
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Data da última atualização
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    @OneToMany(mappedBy = "turma")
    private List<Alocacao> alocacoes;

    public Turma() {
    }

    public Turma(String codigo, Integer periodo, Integer ano, Integer numeroAlunos) {
        this.codigo = codigo;
        this.periodo = periodo;
        this.ano = ano;
        this.numeroAlunos = numeroAlunos;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public Integer getAno() {
        return ano;
    }

    public Integer getNumeroAlunos() {
        return numeroAlunos;
    }

    public Curso getCurso() {
        return curso;
    }

    public List<Alocacao> getAlocacoes() {
        return alocacoes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public void setNumeroAlunos(Integer numeroAlunos) {
        this.numeroAlunos = numeroAlunos;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setAlocacoes(List<Alocacao> alocacoes) {
        this.alocacoes = alocacoes;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Turma other = (Turma) obj;

        return id != null && id.equals(other.id);
    }
}