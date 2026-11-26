package com.fatec.gini.domain.entities;

public enum DiaSemana {
    SEGUNDA,
    TERÇA,
    QUARTA,
    QUINTA,
    SEXTA,
    SÁBADO,
    DOMINGO;

    public DiaSemana anterior() {
        return switch (this) {
            case DOMINGO -> SÁBADO;
            case SEGUNDA -> DOMINGO;
            case TERÇA -> SEGUNDA;
            case QUARTA -> TERÇA;
            case QUINTA -> QUARTA;
            case SEXTA -> QUINTA;
            case SÁBADO -> SEXTA;
        };
    }

    public DiaSemana posterior() {
        return switch (this) {
            case DOMINGO -> SEGUNDA;
            case SEGUNDA -> TERÇA;
            case TERÇA -> QUARTA;
            case QUARTA -> QUINTA;
            case QUINTA -> SEXTA;
            case SEXTA -> SÁBADO;
            case SÁBADO -> DOMINGO;
        };
    }

   
}