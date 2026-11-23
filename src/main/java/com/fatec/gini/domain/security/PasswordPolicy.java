package com.fatec.gini.domain.security;

public final class PasswordPolicy {

    public static final int MIN_LENGTH = 8;
    public static final String MESSAGE = "A senha deve ter pelo menos 8 caracteres, uma letra maiúscula e uma letra minúscula.";

    private PasswordPolicy() {}

    public static boolean isStrong(String password) {
        if (password == null || password.codePointCount(0, password.length()) < MIN_LENGTH) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        for (int offset = 0; offset < password.length();) {
            int codePoint = password.codePointAt(offset);
            hasUppercase |= Character.isUpperCase(codePoint);
            hasLowercase |= Character.isLowerCase(codePoint);
            offset += Character.charCount(codePoint);
        }
        return hasUppercase && hasLowercase;
    }

    public static void requireStrong(String password) {
        if (!isStrong(password)) {
            throw new IllegalArgumentException(MESSAGE);
        }
    }
}
