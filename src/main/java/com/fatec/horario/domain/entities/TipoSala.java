package com.fatec.horario.domain.entities;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
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

    @Column(name = "id_tipo_sala")
    private Long id_tipo_sala;

    private String nome;

    // Construtor vazio obrigatório para o JPA
    public TipoSala() {
    }

    // Construtor com campo principal

    public TipoSala(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Long getid_tipo_sala() {
        return id_tipo_sala;
    }

    public void setid_tipo_sala(Long id_tipo_sala) {
        this.id_tipo_sala = id_tipo_sala;
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
        result = prime * result + ((id_tipo_sala == null) ? 0 : id_tipo_sala.hashCode());
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
        if (id_tipo_sala == null) {
            if (other.id_tipo_sala != null)
                return false;
        } else if (!id_tipo_sala.equals(other.id_tipo_sala))
            return false;
        return true;
    }

}