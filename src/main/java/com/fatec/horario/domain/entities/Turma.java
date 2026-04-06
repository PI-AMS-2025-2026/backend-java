package com.fatec.horario.domain.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "turma")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turma")
    private Long idTurma;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private Integer periodo; // corrigido para Integer

    @Column(nullable = false)
    private Integer ano;

    @Column(name = "numero_alunos", nullable = false)
    private Integer numeroAlunos;

    // Muitas turmas pertencem a um curso
    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso; // corrigido nome

    // Uma turma pode ter várias alocações
    @OneToMany(mappedBy = "turma")
    private List<Alocacao> alocacoes;

    // Construtor vazio (JPA)
    public Turma() {
    }

    // Construtor completo
    public Turma(Long idTurma, String codigo, Integer periodo, Integer ano, Integer numeroAlunos, Curso curso) {
        this.idTurma = idTurma;
        this.codigo = codigo;
        this.periodo = periodo;
        this.ano = ano;
        this.numeroAlunos = numeroAlunos;
        this.curso = curso;
    }

    public Long getIdTurma() {
        return idTurma;
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

    public void setIdTurma(Long idTurma) {
        this.idTurma = idTurma;
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

    @Override
    public int hashCode() {
        return idTurma == null ? 0 : idTurma.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Turma)) return false;
        Turma other = (Turma) obj;
        return idTurma != null && idTurma.equals(other.idTurma);
    }
}
