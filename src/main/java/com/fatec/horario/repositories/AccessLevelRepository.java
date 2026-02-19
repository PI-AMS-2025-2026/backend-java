package com.fatec.horario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.entities.AccessLevel;

@Repository
public interface AccessLevelRepository extends JpaRepository<AccessLevel, Long> {
}
