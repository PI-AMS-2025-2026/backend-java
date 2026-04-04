package com.fatec.horario.domain.entities;

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

        return Status.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
