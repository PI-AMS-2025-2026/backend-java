package com.fatec.horario.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ProdSecurityGuard {

    private final Environment environment;

    @Value("${app.security.debug-role-enabled:false}")
    private boolean debugRoleEnabled;

    @Value("${app.security.jwt-secret}")
    private String jwtSecret;

    public ProdSecurityGuard(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    void validate() {
        boolean prodProfile = Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> "prod".equalsIgnoreCase(profile));

        if (!prodProfile) {
            return;
        }

        if (debugRoleEnabled) {
            throw new IllegalStateException(
                    "Configuracao insegura: app.security.debug-role-enabled nao pode ser true no profile prod");
        }

        if (jwtSecret != null && jwtSecret.contains("CHANGEME")) {
            throw new IllegalStateException("Configuracao insegura: app.security.jwt-secret padrao nao pode ser usado em prod");
        }
    }
}
