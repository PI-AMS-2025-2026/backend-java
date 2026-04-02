package com.fatec.horario.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_usuario")
public class TipoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_tipo_usuario;

    @Column(nullable = false)
    private String nome;

    // Construtor vazio
    public TipoUsuario() {
    }

    public TipoUsuario(Long id_tipo_usuario, String nome) {
        this.id_tipo_usuario = id_tipo_usuario;
        this.nome = nome;
    }

    public Long getId_tipo_usuario() {
        return id_tipo_usuario;
    }

    public void setId_tipo_usuario(Long id_tipo_usuario) {
        this.id_tipo_usuario = id_tipo_usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}