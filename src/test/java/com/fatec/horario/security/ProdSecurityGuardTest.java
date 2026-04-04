package com.fatec.horario.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

class ProdSecurityGuardTest {

    @Test
    void deveFalharQuandoBypassDebugEstiverAtivoEmProd() {
        Environment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod");
        ((MockEnvironment) environment).setActiveProfiles("prod");

        ProdSecurityGuard guard = new ProdSecurityGuard(environment);
        ReflectionTestUtils.setField(guard, "debugRoleEnabled", true);
        ReflectionTestUtils.setField(guard, "jwtSecret", "PROD_SECRET_VALIDA_32_CHARS_MINIMUM_ABC");

        assertThrows(IllegalStateException.class, guard::validate);
    }

    @Test
    void deveFalharQuandoJwtSecretPadraoForUsadaEmProd() {
        Environment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod");
        ((MockEnvironment) environment).setActiveProfiles("prod");

        ProdSecurityGuard guard = new ProdSecurityGuard(environment);
        ReflectionTestUtils.setField(guard, "debugRoleEnabled", false);
        ReflectionTestUtils.setField(guard, "jwtSecret", "CHANGEME_DEV_ONLY_SECRET_32_CHARS_MINIMUM_XYZ");

        assertThrows(IllegalStateException.class, guard::validate);
    }

    @Test
    void devePermitirConfiguracaoSeguraEmProd() {
        Environment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod");
        ((MockEnvironment) environment).setActiveProfiles("prod");

        ProdSecurityGuard guard = new ProdSecurityGuard(environment);
        ReflectionTestUtils.setField(guard, "debugRoleEnabled", false);
        ReflectionTestUtils.setField(guard, "jwtSecret", "PROD_SECRET_SEGURA_COM_32_CHARS_12345");

        assertDoesNotThrow(guard::validate);
    }
}
