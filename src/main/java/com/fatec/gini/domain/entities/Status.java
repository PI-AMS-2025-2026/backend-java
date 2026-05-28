package com.fatec.gini.domain.entities;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    ATIVO,
    INATIVO;

    @JsonCreator
    public static Status fromValue(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Status.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Status inválido: " + value, exception);
        }
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
