package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.domain.entities.user.Usuario;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

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
        WHERE (:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
          AND (:status IS NULL OR u.status = :status)
          AND (:tipoUsuario IS NULL OR u.tipo_usuario.id = :tipoUsuario)
        """)
    Page<Usuario> buscarPorFiltros(
        @Param("nome") String nome,
        @Param("email") String email,
        @Param("status") Status status,
        @Param("tipoUsuario") Long tipoUsuario,
        Pageable pageable);


    UserDetails findByEmail(String email);

}