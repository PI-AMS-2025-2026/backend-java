package com.fatec.horario.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String periodicidade;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer duracao;

    public Curso() {
    }

    public Curso(Long id, String nome, String periodicidade, String status, Integer duracao) {
        this.id = id;
        this.nome = nome;
        this.periodicidade = periodicidade;
        this.status = status;
        this.duracao = duracao;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Curso other = (Curso) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    /*
     * as classes Turmas, Disciplina e GradeHoraria não foram criadas ou não estão
     * na branch dev
     * 
     * @OneToMany(mappedBy = "curso")
     * private List<Turma> turmas;
     * 
     * @OneToMany(mappedBy = "curso")
     * private List<Disciplina> disciplinas;
     * 
     * @OneToMany(mappedBy = "curso")
     * private List<GradeHoraria> gradeHorarias;
     */

    /*
     * public List<Turma> getTurmas() {
     * return turmas;
     * }
     * 
     * public void setTurmas(List<Turma> turmas) {
     * this.turmas = turmas;
     * }
     * 
     * public List<Disciplina> getDisciplinas() {
     * return disciplinas;
     * }
     * 
     * public void setDisciplinas(List<Disciplina> disciplinas) {
     * this.disciplinas = disciplinas;
     * }
     * 
     * public List<GradeHoraria> getGradeHorarias() {
     * return gradeHorarias;
     * }
     * 
     * public void setGradeHorarias(List<GradeHoraria> gradeHorarias) {
     * this.gradeHorarias = gradeHorarias;
     * }
     */

}