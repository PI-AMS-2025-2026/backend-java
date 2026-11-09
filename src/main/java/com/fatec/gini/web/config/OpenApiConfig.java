package com.fatec.gini.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
/**
 * Metadados do OpenAPI, incluindo o esquema de autenticação Bearer/JWT que
 * habilita o botão "Authorize" no Swagger UI.
 *
 * Antes esta classe estava restrita ao perfil "test", de modo que a
 * documentação servida nos demais perfis não declarava o esquema de segurança.
 * Agora acompanha o mesmo interruptor da documentação (app.swagger.enabled).
 */
@ConditionalOnProperty(name = "app.swagger.enabled", havingValue = "true")
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info().title("GINI API").version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
