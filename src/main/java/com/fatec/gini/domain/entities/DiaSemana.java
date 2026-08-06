package com.fatec.gini.domain.entities;

public enum DiaSemana {
    DOMINGO,
    SEGUNDA,
    TERCA,
    QUARTA,
    QUINTA,
    SEXTA,
    SABADO;

    public DiaSemana anterior() {
        return switch (this) {
            case DOMINGO -> SABADO;
            case SEGUNDA -> DOMINGO;
            case TERCA -> SEGUNDA;
            case QUARTA -> TERCA;
            case QUINTA -> QUARTA;
            case SEXTA -> QUINTA;
            case SABADO -> SEXTA;
        };
    }

    public DiaSemana posterior() {
        return switch (this) {
            case DOMINGO -> SEGUNDA;
            case SEGUNDA -> TERCA;
            case TERCA -> QUARTA;
            case QUARTA -> QUINTA;
            case QUINTA -> SEXTA;
            case SEXTA -> SABADO;
            case SABADO -> DOMINGO;
        };
    }
}