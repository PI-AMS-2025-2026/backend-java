package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.Sala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
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
}