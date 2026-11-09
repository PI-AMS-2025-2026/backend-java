package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;

/**
 * Cobre a geração e a verificação dos tokens JWT.
 *
 * Além dos casos básicos (assinatura e expiração), garante o isolamento entre
 * ambientes: os três ambientes hospedados compartilham o perfil prod e o
 * emissor do token é derivado de APP_ENVIRONMENT. Como o emissor também é
 * validado na verificação, um token emitido em um ambiente não pode valer em
 * outro — nem quando os dois usam o mesmo segredo.
 */
class TokenServiceTest {

    private static final String SEGREDO_COMPARTILHADO = "0123456789abcdef0123456789abcdef";
    private static final String EMISSOR_PADRAO = "fatec-gini-test";

    private TokenService tokenService;
    private Usuario usuario;

    /**
     * O emissor é sempre definido explicitamente: em produção ele vem de
     * api.security.token.issuer, que não é resolvido em teste unitário.
     */
    private TokenService tokenService(String emissor, String segredo) {

        TokenService service = new TokenService();
        ReflectionTestUtils.setField(service, "secret", segredo);
        ReflectionTestUtils.setField(service, "issuer", emissor);
        ReflectionTestUtils.setField(service, "tokenExpirationMs", 60_000L);
        return service;
    }

    @BeforeEach
    void setUp() {
        tokenService = tokenService(EMISSOR_PADRAO, "test-secret");
        usuario = new Usuario("Admin", "admin@gini.local", "senha", Status.ATIVO,
                TipoUsuario.ADMINISTRADOR);
    }

    @Test
    void deveGerarTokenComEmailESessao() {
        String token = tokenService.gerarToken(usuario, "refresh-123");

        assertThat(tokenService.validarToken(token)).isEqualTo("admin@gini.local");
        assertThat(tokenService.validarSessao(token)).isEqualTo("refresh-123");
    }

    @Test
    void deveRejeitarTokenComAssinaturaInvalida() {
        String token = tokenService.gerarToken(usuario);
        // Mesmo emissor e outro segredo: isola a assinatura como causa da recusa.
        TokenService outroServico = tokenService(EMISSOR_PADRAO, "outro-secret");

        assertThat(outroServico.validarToken(token)).isEmpty();
        assertThat(outroServico.validarSessao(token)).isNull();
    }

    @Test
    void deveRejeitarTokenExpirado() {
        ReflectionTestUtils.setField(tokenService, "tokenExpirationMs", -1L);
        String token = tokenService.gerarToken(usuario);

        assertThat(tokenService.validarToken(token)).isEmpty();
    }

    @Test
    @DisplayName("Token do ambiente dev é recusado em produção, mesmo com o segredo idêntico")
    void naoDeveValidarTokenDeOutroAmbiente() {

        TokenService servicoDev = tokenService("fatec-gini-dev", SEGREDO_COMPARTILHADO);
        TokenService servicoProducao = tokenService("fatec-gini-production", SEGREDO_COMPARTILHADO);

        String tokenDev = servicoDev.gerarToken(usuario);

        assertThat(servicoDev.validarToken(tokenDev)).isEqualTo("admin@gini.local");
        // validarToken devolve string vazia quando a verificação falha.
        assertThat(servicoProducao.validarToken(tokenDev)).isEmpty();
    }

    @Test
    @DisplayName("A sessão também é recusada quando o token vem de outro ambiente")
    void naoDeveValidarSessaoDeOutroAmbiente() {

        TokenService servicoDev = tokenService("fatec-gini-dev", SEGREDO_COMPARTILHADO);
        TokenService servicoProducao = tokenService("fatec-gini-production", SEGREDO_COMPARTILHADO);

        String tokenDev = servicoDev.gerarToken(usuario, "sessao-123");

        assertThat(servicoDev.validarSessao(tokenDev)).isEqualTo("sessao-123");
        // validarSessao devolve null quando a verificação falha.
        assertThat(servicoProducao.validarSessao(tokenDev)).isNull();
    }

    @Test
    @DisplayName("O emissor do token reflete o ambiente configurado")
    void deveRegistrarAmbienteNoEmissor() {

        String tokenDev = tokenService("fatec-gini-dev", SEGREDO_COMPARTILHADO).gerarToken(usuario);
        String tokenProducao = tokenService("fatec-gini-production", SEGREDO_COMPARTILHADO)
                .gerarToken(usuario);

        assertThat(JWT.decode(tokenDev).getIssuer()).isEqualTo("fatec-gini-dev");
        assertThat(JWT.decode(tokenProducao).getIssuer()).isEqualTo("fatec-gini-production");
    }
}
