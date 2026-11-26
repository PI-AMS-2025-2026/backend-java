package com.fatec.gini.web.validation;

import org.springframework.util.StringUtils;

import com.fatec.gini.domain.security.PasswordPolicy;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SenhaForteValidator implements ConstraintValidator<SenhaForte, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Senha opcional vazia representa "manter a senha atual" na edição.
        return !StringUtils.hasText(value) || PasswordPolicy.isStrong(value);
    }
}
