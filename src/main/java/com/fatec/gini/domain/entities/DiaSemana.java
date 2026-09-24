package com.fatec.gini.domain.entities;

public enum DiaSemana {
    SEGUNDA,
    TERÇA,
    QUARTA,
    QUINTA,
    SEXTA,
    SABADO,
    DOMINGO;

    public DiaSemana anterior() {
        return switch (this) {
            case DOMINGO -> SABADO;
            case SEGUNDA -> DOMINGO;
            case TERÇA -> SEGUNDA;
            case QUARTA -> TERÇA;
            case QUINTA -> QUARTA;
            case SEXTA -> QUINTA;
            case SABADO -> SEXTA;
        };
    }

    public DiaSemana posterior() {
        return switch (this) {
            case DOMINGO -> SEGUNDA;
            case SEGUNDA -> TERÇA;
            case TERÇA -> QUARTA;
            case QUARTA -> QUINTA;
            case QUINTA -> SEXTA;
            case SEXTA -> SABADO;
            case SABADO -> DOMINGO;
        };
    }

   
}