package com.fatec.horario.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RECURSO")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecurso;

    private String nome;

    private String tipo;

    public Recurso() {}

    public Recurso(String nome, String tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    // Getters e Setters
    public Long getIdRecurso() {
        return idRecurso;
    }

    public void setIdRecurso(Long idRecurso) {
        this.idRecurso = idRecurso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idRecurso == null) ? 0 : idRecurso.hashCode());
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
        Recurso other = (Recurso) obj;
        if (idRecurso == null) {
            if (other.idRecurso != null)
                return false;
        } else if (!idRecurso.equals(other.idRecurso))
            return false;
        return true;
    }



}