package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fatec.gini.domain.entities.RefreshToken;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;
import com.fatec.gini.infrastructure.repositories.RefreshTokenRepository;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;
import com.fatec.gini.web.exception.RefreshTokenException;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void deveRotacionarRefreshTokenValidoEEmitirNovosTokens() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationMs", 60_000L);
        Usuario usuario = new Usuario("Coord", "coord@fatec.local", "senha", Status.ATIVO, TipoUsuario.COORDENADOR);
        usuario.setId(2L);
        RefreshToken atual = new RefreshToken();
        atual.setUsuario(usuario);
        atual.setToken("refresh-antigo");
        atual.setExpiryDate(java.time.Instant.now().plusSeconds(600));

        when(refreshTokenRepository.findByToken("refresh-antigo")).thenReturn(Optional.of(atual));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(refreshTokenRepository.findByUsuario(usuario)).thenReturn(Optional.of(atual));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenService.gerarToken(org.mockito.ArgumentMatchers.eq(usuario), any()))
                .thenReturn("access-novo");

        var resposta = refreshTokenService.refresh("refresh-antigo");

        assertThat(resposta.accessToken()).isEqualTo("access-novo");
        assertThat(resposta.refreshToken()).isNotEqualTo("refresh-antigo").isNotBlank();
        assertThat(resposta.usuario().tipoUsuario()).isEqualTo(TipoUsuario.COORDENADOR);
        assertThat(atual.getToken()).isEqualTo(resposta.refreshToken());
    }

    @Test
    void deveRejeitarEExcluirRefreshTokenExpirado() {
        Usuario usuario = new Usuario("Coord", "coord@fatec.local", "senha", Status.ATIVO, TipoUsuario.COORDENADOR);
        RefreshToken expirado = new RefreshToken();
        expirado.setUsuario(usuario);
        expirado.setToken("refresh-expirado");
        expirado.setExpiryDate(java.time.Instant.now().minusSeconds(1));
        when(refreshTokenRepository.findByToken("refresh-expirado")).thenReturn(Optional.of(expirado));

        assertThatThrownBy(() -> refreshTokenService.refresh("refresh-expirado"))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessageContaining("expirado");
        verify(refreshTokenRepository).delete(expirado);
    }

    @Test
    void deveRejeitarRefreshTokenDesconhecido() {
        when(refreshTokenRepository.findByToken("desconhecido")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.refresh("desconhecido"))
                .isInstanceOf(RefreshTokenException.class);
    }

    @Test
    void deveRotacionarTokenEmLoginsConsecutivosDoMesmoUsuario() {
        Usuario usuario = new Usuario(
                "Administrador",
                "admin@fatec.sp.gov.br",
                "senha",
                Status.ATIVO,
                TipoUsuario.ADMINISTRADOR);
        usuario.setId(1L);

        AtomicReference<RefreshToken> tokenPersistido = new AtomicReference<>();

        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationMs", 60_000L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(refreshTokenRepository.findByUsuario(usuario))
                .thenAnswer(invocation -> Optional.ofNullable(tokenPersistido.get()));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> {
                    RefreshToken token = invocation.getArgument(0);
                    tokenPersistido.set(token);
                    return token;
                });

        RefreshToken primeiroLogin = refreshTokenService.createRefreshToken(1L);
        String tokenAnterior = primeiroLogin.getToken();
        RefreshToken segundoLogin = refreshTokenService.createRefreshToken(1L);

        assertThat(segundoLogin).isSameAs(primeiroLogin);
        assertThat(segundoLogin.getToken()).isNotBlank();
        assertThat(segundoLogin.getToken()).isNotEqualTo(tokenAnterior);
        assertThat(segundoLogin.getExpiryDate()).isAfter(java.time.Instant.now());
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
        verify(refreshTokenRepository, times(2)).save(primeiroLogin);
    }
}
