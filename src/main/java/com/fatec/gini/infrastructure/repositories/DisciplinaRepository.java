package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.Disciplina;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    @Query("SELECT d FROM Disciplina d WHERE " +
            "(COALESCE(:nome, '') = '' OR LOWER(d.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:idCurso IS NULL OR d.curso.id = :idCurso) AND " +
            "(:idTipoSala IS NULL OR d.tipoSala.id = :idTipoSala) AND " +
            "(COALESCE(:tipoDisciplina, '') = '' OR LOWER(d.tipoDisciplina) = LOWER(:tipoDisciplina)) AND " +
            "(:periodo IS NULL OR d.periodo = :periodo) AND " +
            "(COALESCE(:modalidade, '') = '' OR LOWER(d.modalidade) = LOWER(:modalidade)) AND " +
            "(COALESCE(:cor, '') = '' OR LOWER(d.cor) = LOWER(:cor)) AND " +
            "(COALESCE(:busca, '') = '' OR LOWER(d.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(d.curso.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(d.codDisciplina) LIKE LOWER(CONCAT('%', :busca, '%')))")
    Page<Disciplina> findByFiltros(
            @Param("nome") String nome,
            @Param("idCurso") Long idCurso,
            @Param("idTipoSala") Long idTipoSala,
            @Param("tipoDisciplina") String tipoDisciplina,
            @Param("periodo") Integer periodo,
            @Param("modalidade") String modalidade,
            @Param("cor") String cor,
            @Param("busca") String busca,
            Pageable pageable);

    // CORREÇÃO: usados no service para bloquear codDisciplina duplicado com
    // mensagem explícita
    boolean existsByCodDisciplina(String codDisciplina);

    boolean existsByCodDisciplinaAndIdNot(String codDisciplina, Long id);

    // CORREÇÃO: duplicidade considera nome, período, tipo e curso (não apenas o nome)
boolean existsByNomeIgnoreCaseAndPeriodoAndTipoDisciplinaIgnoreCaseAndCursoId(
        String nome, Integer periodo, String tipoDisciplina, Long cursoId);

boolean existsByNomeIgnoreCaseAndPeriodoAndTipoDisciplinaIgnoreCaseAndCursoIdAndIdNot(
        String nome, Integer periodo, String tipoDisciplina, Long cursoId, Long id);
}
