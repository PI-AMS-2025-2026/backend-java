package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.fatec.gini.domain.entities.PasswordResetToken;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;
import com.fatec.gini.infrastructure.repositories.PasswordResetTokenRepository;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;
import com.fatec.gini.web.exception.ParameterException;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    private PasswordResetService passwordResetService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetService(
                usuarioRepository,
                passwordResetTokenRepository,
                passwordEncoder,
                emailService);
        ReflectionTestUtils.setField(passwordResetService, "expirationMinutes", 15L);

        usuario = new Usuario("Administrador", "admin@gini.local", "senha-antiga", Status.ATIVO,
                TipoUsuario.ADMINISTRADOR);
        usuario.setId(1L);
    }

    @Test
    void deveCriarTokenEEnviarEmailParaUsuarioExistente() {
        when(usuarioRepository.findByEmail("admin@gini.local")).thenReturn(usuario);
        when(passwordResetTokenRepository.findAllByUsuarioAndUsedFalse(usuario)).thenReturn(List.of());

        passwordResetService.solicitarRecuperacao("admin@gini.local");

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());
        verify(emailService).enviarRecuperacaoSenha(any(), any());

        PasswordResetToken token = tokenCaptor.getValue();
        assertThat(token.getUsuario()).isSameAs(usuario);
        assertThat(token.getTokenHash()).hasSize(64);
        assertThat(token.getExpiresAt()).isAfter(Instant.now());
        assertThat(token.isUsed()).isFalse();
    }

    @Test
    void deveRedefinirSenhaEInvalidarToken() {
        PasswordResetToken token = new PasswordResetToken(usuario, "hash", Instant.now().plusSeconds(60), Instant.now());
        when(passwordResetTokenRepository.findByTokenHashAndUsedFalse(any())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("nova-senha"))
                .thenReturn("senha-codificada");

        passwordResetService.redefinirSenha("token-bruto", "nova-senha");

        assertThat(usuario.getSenha()).isEqualTo("senha-codificada");
        assertThat(token.isUsed()).isTrue();
        assertThat(token.getUsedAt()).isNotNull();
        verify(usuarioRepository).save(usuario);
        verify(passwordResetTokenRepository).save(token);
    }

    @Test
    void deveRejeitarTokenExpirado() {
        PasswordResetToken token = new PasswordResetToken(usuario, "hash", Instant.now().minusSeconds(1), Instant.now());
        when(passwordResetTokenRepository.findByTokenHashAndUsedFalse(any())).thenReturn(Optional.of(token));

        assertThrows(ParameterException.class,
                () -> passwordResetService.redefinirSenha("token-bruto", "nova-senha"));
        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveRevelarSeEmailNaoExiste() {
        when(usuarioRepository.findByEmail("desconhecido@gini.local")).thenReturn(null);

        passwordResetService.solicitarRecuperacao("desconhecido@gini.local");

        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).enviarRecuperacaoSenha(any(), any());
    }
}