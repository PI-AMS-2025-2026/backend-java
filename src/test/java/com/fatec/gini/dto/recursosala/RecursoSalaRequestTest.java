package com.fatec.gini.dto.recursosala;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fatec.gini.dto.recursoSala.RecursoSalaRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class RecursoSalaRequestTest {

    private Validator validator;

    @BeforeEach
    void configurarValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void deveInformarQueQuantidadeDeveSerMaiorQueZero() {
        RecursoSalaRequest request =
                new RecursoSalaRequest(1L, 1L, 0);

        Set<ConstraintViolation<RecursoSalaRequest>> violations =
                validator.validate(request);

        assertThat(mensagens(violations))
                .contains("O campo quantidade deve ser maior que zero.");
    }

    @Test
    void deveInformarQueSalaIdEObrigatorioQuandoNaoForInformado() {
        RecursoSalaRequest request =
                new RecursoSalaRequest(null, 1L, 1);

        Set<ConstraintViolation<RecursoSalaRequest>> violations =
                validator.validate(request);

        assertThat(mensagens(violations))
                .contains("SalaId é obrigatório.");
    }

    @Test
    void deveAceitarRequestValido() {
        RecursoSalaRequest request =
                new RecursoSalaRequest(1L, 1L, 1);

        assertThat(validator.validate(request))
                .isEmpty();
    }

    private Set<String> mensagens(
            Set<ConstraintViolation<RecursoSalaRequest>> violations) {

        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}