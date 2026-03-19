package com.fatec.horario.domain.entities;

import jakarta.persistence.*;

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
    private String periodo;

    @Column(nullable = false)
    private Integer ano;

    @Column(name = "numero_alunos", nullable = false)
    private Integer numeroAlunos;

    // Muitas turmas pertencem a um curso
    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Course curso;

    // Construtor vazio (JPA)
    public Turma() {
    }

    // Construtor completo
    public Turma(Long id, String codigo, String periodo, Integer ano, Integer numeroAlunos, Course curso) {
        this.id = id;
        this.codigo = codigo;
        this.periodo = periodo;
        this.ano = ano;
        this.numeroAlunos = numeroAlunos;
        this.curso = curso;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public Integer getAno() {
        return ano;
    }

    public Integer getNumeroAlunos() {
        return numeroAlunos;
    }

    public Course getCurso() {
        return curso;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public void setNumeroAlunos(Integer numeroAlunos) {
        this.numeroAlunos = numeroAlunos;
    }

    public void setCurso(Course curso) {
        this.curso = curso;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Turma)) return false;
        Turma other = (Turma) obj;
        return id != null && id.equals(other.id);
    }
}
