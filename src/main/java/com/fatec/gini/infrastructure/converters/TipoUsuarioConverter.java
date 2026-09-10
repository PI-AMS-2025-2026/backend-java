package com.fatec.gini.infrastructure.converters;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fatec.gini.domain.models.TipoUsuario;

@Component
public class TipoUsuarioConverter implements Converter<String, TipoUsuario> {

    @Override
    public TipoUsuario convert(String source) {
        if (source == null) {
            return null;
        }

        return TipoUsuario.valueOf(
                source.trim().toUpperCase(Locale.ROOT));
    }
}