package com.fatec.gini.domain.entities;

public enum TipoUsuario {
    ADMINISTRADOR("admin"),
    COORDENADOR("coordenador");

    private String tipo;

    TipoUsuario(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }  
}
