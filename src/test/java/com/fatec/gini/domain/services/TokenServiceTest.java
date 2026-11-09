package com.fatec.gini.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;

/**
 * Garante o isolamento dos tokens entre ambientes.
 *
 * Os ambientes hospedados compartilham o perfil prod, e o emissor do token é
 * derivado de APP_ENVIRONMENT. Como o emissor também é validado na verificação,
 * um token emitido em um ambiente não pode valer em outro — nem quando os dois
 * usam o mesmo segredo.
 */
class TokenServiceTest {

    private static final String SEGREDO_COMPARTILHADO = "0123456789abcdef0123456789abcdef";

    private TokenService tokenService(String emissor, String segredo) {

        TokenService service = new TokenService();
        ReflectionTestUtils.setField(service, "secret", segredo);
        ReflectionTestUtils.setField(service, "issuer", emissor);
        ReflectionTestUtils.setField(service, "tokenExpirationMs", 86_400_000L);
        return service;
    }

    private Usuario usuario() {
        return new Usuario("Admin", "adm@gini.com", "123456", Status.ATIVO, TipoUsuario.ADMINISTRADOR);
    }

    @Test
    @DisplayName("Token emitido no mesmo ambiente é válido")
    void deveValidarTokenDoMesmoAmbiente() {

        TokenService serviceDev = tokenService("fatec-gini-dev", SEGREDO_COMPARTILHADO);

        String token = serviceDev.gerarToken(usuario());

        assertEquals("adm@gini.com", serviceDev.validarToken(token));
    }

    @Test
    @DisplayName("Token do ambiente dev é recusado em produção, mesmo com o segredo idêntico")
    void naoDeveValidarTokenDeOutroAmbiente() {

        TokenService serviceDev = tokenService("fatec-gini-dev", SEGREDO_COMPARTILHADO);
        TokenService serviceProducao = tokenService("fatec-gini-production", SEGREDO_COMPARTILHADO);

        String tokenDev = serviceDev.gerarToken(usuario());

        // validarToken devolve string vazia quando a verificação falha.
        assertEquals("", serviceProducao.validarToken(tokenDev));
    }

    @Test
    @DisplayName("O emissor do token reflete o ambiente configurado")
    void deveRegistrarAmbienteNoEmissor() {

        String tokenDev = tokenService("fatec-gini-dev", SEGREDO_COMPARTILHADO).gerarToken(usuario());
        String tokenProducao = tokenService("fatec-gini-production", SEGREDO_COMPARTILHADO)
                .gerarToken(usuario());

        assertEquals("fatec-gini-dev", JWT.decode(tokenDev).getIssuer());
        assertEquals("fatec-gini-production", JWT.decode(tokenProducao).getIssuer());
        assertNotEquals(JWT.decode(tokenDev).getIssuer(), JWT.decode(tokenProducao).getIssuer());
    }

    @Test
    @DisplayName("A sessão também é recusada quando o token vem de outro ambiente")
    void naoDeveValidarSessaoDeOutroAmbiente() {

        TokenService serviceDev = tokenService("fatec-gini-dev", SEGREDO_COMPARTILHADO);
        TokenService serviceProducao = tokenService("fatec-gini-production", SEGREDO_COMPARTILHADO);

        String tokenDev = serviceDev.gerarToken(usuario(), "sessao-123");

        assertEquals("sessao-123", serviceDev.validarSessao(tokenDev));
        // validarSessao devolve null quando a verificação falha.
        assertEquals(null, serviceProducao.validarSessao(tokenDev));
    }
}
