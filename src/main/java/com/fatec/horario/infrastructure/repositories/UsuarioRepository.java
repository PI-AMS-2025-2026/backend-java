package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.domain.entities.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByEmailIgnoreCase(String email);

  boolean existsByEmailIgnoreCase(String email);

    /**
     * Retorna usuários aplicando filtros opcionais.
     *
     * Regras dos filtros:
     * - nome: busca parcial, ignorando maiúsculas/minúsculas.
     * - email: busca parcial, ignorando maiúsculas/minúsculas.
     * - cidade: busca parcial, ignorando maiúsculas/minúsculas.
     * - status: comparação exata, respeitando maiúsculas/minúsculas.
     * - tipoUsuarioId: comparação exata.
     * - tipoUsuarioNome: busca parcial por nome do tipo de usuário, ignorando maiúsculas/minúsculas.
     *
     * Quando um parâmetro é null, o filtro correspondente é ignorado.
     */
    @Query("""
        SELECT u
        FROM Usuario u
        LEFT JOIN u.tipo_usuario t
        WHERE (:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
          AND (:cidade IS NULL OR LOWER(u.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))
          AND (:status IS NULL OR u.status = :status)
          AND (:tipoUsuarioId IS NULL OR u.tipo_usuario.id = :tipoUsuarioId)
          AND (:tipoUsuarioNome IS NULL OR LOWER(t.nome) LIKE LOWER(CONCAT('%', :tipoUsuarioNome, '%')))
        """)
    Page<Usuario> buscarPorFiltros(
        @Param("nome") String nome,
        @Param("email") String email,
        @Param("cidade") String cidade,
        @Param("status") Status status,
        @Param("tipoUsuarioId") Long tipoUsuarioId,
        @Param("tipoUsuarioNome") String tipoUsuarioNome,
        Pageable pageable);

}