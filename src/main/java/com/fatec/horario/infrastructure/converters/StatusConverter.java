package com.fatec.horario.infrastructure.converters;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fatec.horario.domain.entities.Status;

@Component
public class StatusConverter implements Converter<String, Status> {

    @Override
    public Status convert(String source) {
        if (source == null) {
            return null;
        }

        return Status.valueOf(source.trim().toUpperCase(Locale.ROOT));
    }
}
