package com.fatec.horario.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.model.AccessLevel;

@Repository
public interface AccessLevelRepository extends JpaRepository<AccessLevel, Long> {
}
