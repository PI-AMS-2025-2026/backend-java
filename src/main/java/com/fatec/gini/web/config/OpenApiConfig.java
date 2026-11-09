package com.fatec.gini.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import io.swagger.v3.core.jackson.ModelResolver;
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

    /**
     * Alinha os nomes de campo da documentação com os que a API realmente usa.
     *
     * O springdoc monta os schemas com um ObjectMapper próprio, que ignora
     * spring.jackson.property-naming-strategy. Sem este bean a documentação
     * mostraria camelCase (accessToken) enquanto a API responde em snake_case
     * (access_token), levando o frontend a usar os campos errados.
     *
     * O mapper é construído aqui, e não injetado, porque o Spring Boot 4 expõe
     * o ObjectMapper do Jackson 3 (tools.jackson) enquanto o swagger-core
     * espera o do Jackson 2 (com.fasterxml.jackson).
     *
     * Ao alterar spring.jackson.property-naming-strategy em
     * application.properties, ajuste também a estratégia abaixo.
     */
    @Bean
    ModelResolver modelResolver() {
        ObjectMapper mapperDocumentacao = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return new ModelResolver(mapperDocumentacao);
    }

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
