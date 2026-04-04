package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.domain.entities.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    /**
     * Retorna cursos aplicando filtros opcionais.
     *
     * Regras dos filtros:
     * 
     * @param nome          busca parcial, ignorando maiúsculas/minúsculas.
     * @param periodicidade comparação exata, ignorando maiúsculas/minúsculas.
     * @param status        comparação exata, respeitando maiúsculas/minúsculas.
     * @param duracao       comparação exata.
     *                      </br>
     *                      </br>
     *                      Quando um parâmetro é null, o filtro correspondente é
     *                      ignorado.
     */
    @Query("""
            SELECT c
            FROM Curso c
            WHERE (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
              AND (:periodicidade IS NULL OR LOWER(c.periodicidade) = LOWER(:periodicidade))
                                                        AND (:status IS NULL OR c.status = :status)
              AND (:duracao IS NULL OR c.duracao = :duracao)
            """)
    Page<Curso> buscarPorFiltros(
            @Param("nome") String nome,
            @Param("periodicidade") String periodicidade,
            @Param("status") Status status,
            @Param("duracao") Integer duracao,
            Pageable pageable);
}