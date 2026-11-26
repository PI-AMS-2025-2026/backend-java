package com.fatec.gini.web.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError {
    private List<String> errors = new ArrayList<>();
    private List<FieldError> fieldErrors = new ArrayList<>();

    public void addError(String error) {
        this.errors.add(error);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addFieldError(String field, String message) {
        this.fieldErrors.add(new FieldError(field, message));
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public record FieldError(String field, String message) {}
}
