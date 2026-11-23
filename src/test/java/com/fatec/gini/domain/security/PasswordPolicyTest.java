package com.fatec.gini.domain.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordPolicyTest {

    @Test
    void aceitaOitoCaracteresComLetrasMaiusculaEMinusculaIncluindoAcentos() {
        assertThat(PasswordPolicy.isStrong("Árvoreaz")).isTrue();
        assertThat(PasswordPolicy.isStrong("Senha123")).isTrue();
    }

    @Test
    void rejeitaSenhaCurtaOuSemUmaDasClassesDeLetras() {
        assertThat(PasswordPolicy.isStrong("Abc1234")).isFalse();
        assertThat(PasswordPolicy.isStrong("abcdefgh")).isFalse();
        assertThat(PasswordPolicy.isStrong("ABCDEFGH")).isFalse();
    }
}
