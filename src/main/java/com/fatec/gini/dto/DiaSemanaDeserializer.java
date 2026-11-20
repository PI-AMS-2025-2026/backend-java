package com.fatec.gini.dto;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fatec.gini.domain.entities.DiaSemana;

public class DiaSemanaDeserializer extends JsonDeserializer<DiaSemana> {

    @Override
    public DiaSemana deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() != JsonToken.VALUE_STRING) {
            return (DiaSemana) context.handleUnexpectedToken(DiaSemana.class, parser);
        }

        String value = parser.getText();
        try {
            return DiaSemana.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return (DiaSemana) context.handleWeirdStringValue(
                    DiaSemana.class,
                    value,
                    "Dia da semana deve ser um valor textual canônico");
        }
    }
}
