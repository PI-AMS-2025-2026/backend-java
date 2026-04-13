package com.fatec.horario.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "professor_disciplina",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"id_usuario", "id_disciplina"})
       })
public class ProfessorDisciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfessorDisciplina;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_disciplina")
    private Disciplina disciplina;

    public ProfessorDisciplina() {}

    public ProfessorDisciplina(Usuario usuario, Disciplina disciplina) {
        this.usuario = usuario;
        this.disciplina = disciplina;
    }

    public Long getIdProfessorDisciplina() {
        return idProfessorDisciplina;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }
}
