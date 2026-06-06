package com.fatec.gini.domain.entities;

import com.fatec.gini.domain.entities.user.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//TODO: avaliar se entidade é nescessaria
@Entity
@Table(name = "professor_disciplina")
public class ProfessorDisciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_professor_disciplina")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", unique = true)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_disciplina", unique = true)
    private Disciplina disciplina;

    public ProfessorDisciplina() {
    }

    public ProfessorDisciplina(Usuario usuario, Disciplina disciplina) {
        this.usuario = usuario;
        this.disciplina = disciplina;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
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
        ProfessorDisciplina other = (ProfessorDisciplina) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
