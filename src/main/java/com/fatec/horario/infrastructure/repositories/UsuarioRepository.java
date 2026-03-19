package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Usuario> findByStatus(Boolean status, Pageable pageable);

    Page<Usuario> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}