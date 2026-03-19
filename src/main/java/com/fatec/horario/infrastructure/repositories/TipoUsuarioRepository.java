package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {
}