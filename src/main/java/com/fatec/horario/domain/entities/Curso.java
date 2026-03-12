package com.fatec.horario.domain.entities;

import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, length = 255)
    private String descricao;

    @ManyToMany
    @JoinTable(
        name = "curso_disciplina",
        joinColumns = @JoinColumn(name = "curso_id"),
        inverseJoinColumns = @JoinColumn(name = "disciplina_id")
    )
    private Set<Disciplina> disciplinas;

    @OneToMany(mappedBy = "curso")
    private List<SemestreAcademico> semestresAcademicos;

    public Curso() {
    }

    public Curso(Long id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Set<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(Set<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }

    public List<SemestreAcademico> getSemestresAcademicos() {
        return semestresAcademicos;
    }

    public void setSemestresAcademicos(List<SemestreAcademico> semestresAcademicos) {
        this.semestresAcademicos = semestresAcademicos;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (!(obj instanceof Curso)) return false;

        Curso outro = (Curso) obj;

        return id != null && id.equals(outro.id);
    }
}