package com.fatec.gini.domain.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.PasswordResetToken;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.infrastructure.repositories.PasswordResetTokenRepository;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;
import com.fatec.gini.web.exception.ParameterException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-reset.expiration-minutes:15}")
    private long expirationMinutes;

    @Transactional
    public void solicitarRecuperacao(String email) {
        UserDetails userDetails = usuarioRepository.findByEmail(email.trim());
        if (!(userDetails instanceof Usuario usuario)) {
            return;
        }

        passwordResetTokenRepository.findAllByUsuarioAndUsedFalse(usuario)
                .forEach(token -> {
                    token.setUsed(true);
                    token.setUsedAt(Instant.now());
                });

        String token = gerarToken();
        Instant agora = Instant.now();
        PasswordResetToken passwordResetToken = new PasswordResetToken(
                usuario,
                sha256(token),
                agora.plus(Duration.ofMinutes(expirationMinutes)),
                agora);
        passwordResetTokenRepository.save(passwordResetToken);
        emailService.enviarRecuperacaoSenha(usuario.getEmail(), token);
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository
                .findByTokenHashAndUsedFalse(sha256(token))
                .orElseThrow(() -> new ParameterException("Token de recuperação inválido"));

        if (passwordResetToken.getExpiresAt().isBefore(Instant.now())) {
            passwordResetToken.setUsed(true);
            passwordResetToken.setUsedAt(Instant.now());
            throw new ParameterException("Token de recuperação expirado");
        }

        Usuario usuario = passwordResetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        passwordResetToken.setUsed(true);
        passwordResetToken.setUsedAt(Instant.now());
        usuarioRepository.save(usuario);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    private String gerarToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(digest.length * 2);
            for (byte item : digest) {
                result.append(String.format("%02x", item));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não está disponível", exception);
        }
    }
}