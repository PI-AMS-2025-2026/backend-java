package com.fatec.gini.infrastructure.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.gini.domain.entities.PasswordResetToken;
import com.fatec.gini.domain.entities.Usuario;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);

    List<PasswordResetToken> findAllByUsuarioAndUsedFalse(Usuario usuario);
}