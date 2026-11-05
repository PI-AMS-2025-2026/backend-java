package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;

class TokenServiceTest {

    private TokenService tokenService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret");
        ReflectionTestUtils.setField(tokenService, "tokenExpirationMs", 60_000L);
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
        TokenService outroServico = new TokenService();
        ReflectionTestUtils.setField(outroServico, "secret", "outro-secret");

        assertThat(outroServico.validarToken(token)).isEmpty();
        assertThat(outroServico.validarSessao(token)).isNull();
    }

    @Test
    void deveRejeitarTokenExpirado() {
        ReflectionTestUtils.setField(tokenService, "tokenExpirationMs", -1L);
        String token = tokenService.gerarToken(usuario);

        assertThat(tokenService.validarToken(token)).isEmpty();
    }
}