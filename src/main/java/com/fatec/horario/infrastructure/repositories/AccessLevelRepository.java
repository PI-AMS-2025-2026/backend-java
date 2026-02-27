package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessLevelRepository extends JpaRepository<AccessLevel, Long> {

    Optional<AccessLevel> findByName(String name);
}