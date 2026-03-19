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

    private Long id_recurso;
    
    private String nome;

    private String tipo;

    public Recurso() {
    }

    public Long getId_recurso() {
        return id_recurso;
    }

    public void setId_recurso(Long id_recurso) {
        this.id_recurso = id_recurso;
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
        result = prime * result + ((id_recurso == null) ? 0 : id_recurso.hashCode());
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
        if (id_recurso == null) {
            if (other.id_recurso != null)
                return false;
        } else if (!id_recurso.equals(other.id_recurso))
            return false;
        return true;
    }

    
}
