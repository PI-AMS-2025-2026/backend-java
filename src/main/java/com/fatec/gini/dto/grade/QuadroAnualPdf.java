package com.fatec.gini.dto.grade;

import java.util.List;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroResponse;
import com.fatec.gini.dto.quadroHorario.BlocoQuadroResponse;
import com.fatec.gini.dto.quadroHorario.CelulaGradeResponse;

public record QuadroAnualPdf(CursoGradeResumoResponse curso, int ano, List<DiaSemana> dias, List<Turma> turmas) {
    public record Turma(String codigo, List<Periodo> periodos) {
    }

    public record Periodo(PeriodoAtividadeQuadroResponse dados, int versao,
            List<BlocoQuadroResponse> blocos, List<CelulaGradeResponse> celulas) {
    }
}
