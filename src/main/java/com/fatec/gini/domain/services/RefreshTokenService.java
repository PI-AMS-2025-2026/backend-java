package com.fatec.gini.domain.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.RefreshToken;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.dto.auth.LoginResponse;
import com.fatec.gini.infrastructure.repositories.RefreshTokenRepository;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;
import com.fatec.gini.web.exception.RefreshTokenException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${api.security.token.refreshExpirationMs:604800000}")
    private Long refreshTokenExpirationMs;

    private final UsuarioRepository repository;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    // Mantem um unico refresh token ativo por usuario.
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        Usuario usuario = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + userId));

        refreshTokenRepository.findByUsuario(usuario)
                .ifPresent(refreshTokenRepository::delete);

        var token = new RefreshToken();
        token.setUsuario(usuario);
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        token.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(token);
    }

    // Troca o refresh token valido por um novo par de tokens.
    @Transactional
    public LoginResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RefreshTokenException("Refresh token inválido");
        }

        RefreshToken current = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new RefreshTokenException("Refresh token inválido"));

        Usuario usuario = current.getUsuario();

        if (isTokenExpired(current) || usuario == null || !usuario.isEnabled()) {
            refreshTokenRepository.delete(current);
            throw new RefreshTokenException("Refresh token inválido ou expirado");
        }

        refreshTokenRepository.delete(current);

        RefreshToken novoRefresh = createRefreshToken(usuario.getId());

        String accessToken = tokenService.gerarToken(usuario, novoRefresh.getToken());

        return new LoginResponse(
                accessToken,
                novoRefresh.getToken());
    }

    // Considera invalido o token ausente, sem validade ou ja expirado.
    public boolean isTokenExpired(RefreshToken token) {
        return token == null
                || token.getExpiryDate() == null
                || !token.getExpiryDate().isAfter(Instant.now());
    }

    // Remove o refresh token para impedir sua reutilizacao no logout.
    @Transactional
    public void revoke(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        refreshTokenRepository
                .findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

}
