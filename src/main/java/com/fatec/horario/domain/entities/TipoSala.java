package com.fatec.horario.domain.entities;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Indica que a classe é uma entidade JPA (tabela no banco)
@Entity
// Define o nome da tabela no banco
@Table(name = "TIPO_SALA")
public class TipoSala {

    // Chave primária da tabela
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoSala;

    private String nome;

    // Construtor vazio obrigatório para o JPA
    public TipoSala() {}

    // Construtor com campo principal

    public TipoSala(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Long getIdTipoSala() {
        return idTipoSala;
    }

    public void setIdTipoSala(Long idTipoSala) {
        this.idTipoSala = idTipoSala;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idTipoSala == null) ? 0 : idTipoSala.hashCode());
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
        TipoSala other = (TipoSala) obj;
        if (idTipoSala == null) {
            if (other.idTipoSala != null)
                return false;
        } else if (!idTipoSala.equals(other.idTipoSala))
            return false;
        return true;
    }

}