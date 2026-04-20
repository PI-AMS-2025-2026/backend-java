package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.TipoUsuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {

	/**
	 * Retorna tipos de usuário aplicando filtro opcional por nome.
	 *
	 * Regra do filtro:
	 * - nome: busca parcial, ignorando maiúsculas/minúsculas.
	 *
	 * Quando o parâmetro é null, o filtro é ignorado.
	 */
	@Query("""
			SELECT t
			FROM TipoUsuario t
			WHERE (:nome IS NULL OR LOWER(t.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
			""")
	List<TipoUsuario> buscarPorFiltro(@Param("nome") String nome);
}