package com.fatec.gini.infrastructure.converters;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fatec.gini.domain.entities.DiaSemana;

@Component
public class DiaSemanaConverter implements Converter<String, DiaSemana> {

    @Override
    public DiaSemana convert(String source) {
        if (source == null) {
            return null;
        }

        return DiaSemana.valueOf(
                source.trim().toUpperCase(Locale.ROOT));
    }
}