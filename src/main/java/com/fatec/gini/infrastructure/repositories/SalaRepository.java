package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    @Query("""
            SELECT s FROM Sala s
            WHERE (:idTipoSala IS NULL OR s.tipoSala.id = :idTipoSala)
              AND (:capacidade IS NULL OR s.capacidade >= :capacidade)
            """)
    Page<Sala> buscarPorFiltros(
            @Param("idTipoSala") Long idTipoSala,
            @Param("capacidade") Integer capacidade,
            Pageable pageable);

    /**
     * Verifica se já existe uma sala com o mesmo código,
     * ignorando diferença entre letras maiúsculas e minúsculas.
     */
    boolean existsByCodigoIgnoreCase(String codigo);

    /**
     * Verifica se já existe outra sala com o mesmo código,
     * ignorando diferença entre letras maiúsculas e minúsculas.
     *
     * Utilizado durante a atualização para que a própria sala
     * não seja considerada uma duplicidade.
     */
    boolean existsByCodigoIgnoreCaseAndIdNot(
            String codigo,
            Long id);
}