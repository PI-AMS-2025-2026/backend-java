package com.fatec.gini.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.gini.domain.services.usecase.write.MotorQuadroHorario;
import com.fatec.gini.domain.services.export.MotorQuadroExcelService;
import com.fatec.gini.domain.services.export.MotorQuadroPdfService;
import com.fatec.gini.dto.grade.GradeCursoResponse;
import com.fatec.gini.dto.grade.GradeTurmaResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Motor do Quadro Horário")
@RestController
@RequestMapping("/motor-quadro/cursos/{curso_id}")
@RequiredArgsConstructor
public class MotorQuadroController {

    private final MotorQuadroHorario motorQuadroHorario;
    private final MotorQuadroPdfService motorQuadroPdfService;
    private final MotorQuadroExcelService motorQuadroExcelService;

    @GetMapping
    @Operation(summary = "Consultar grade horária completa de um curso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grade do curso retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Curso ou quadro horário ativo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Mais de um quadro horário ativo encontrado")
    })
    public ResponseEntity<GradeCursoResponse> obterGradeCurso(@PathVariable("curso_id") Long cursoId) {
        return ResponseEntity.ok(motorQuadroHorario.construir(cursoId));
    }

    @GetMapping("/turmas/{turma_id}")
    @Operation(summary = "Consultar horários de uma turma do curso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horários da turma retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Curso, turma ou quadro horário ativo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Mais de um quadro horário ativo encontrado")
    })
    public ResponseEntity<GradeTurmaResponse> obterGradeTurma(
            @PathVariable("curso_id") Long cursoId,
            @PathVariable("turma_id") Long turmaId) {
        return ResponseEntity.ok(motorQuadroHorario.construir(cursoId, turmaId));
    }

        @GetMapping(value = "/exportar/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
        @Operation(summary = "Exportar o quadro horário do curso em PDF")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Curso ou quadro horário ativo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Mais de um quadro horário ativo encontrado")
        })
        public ResponseEntity<byte[]> exportarPdf(@PathVariable("curso_id") Long cursoId) {
        byte[] arquivo = motorQuadroPdfService.exportar(motorQuadroHorario.construir(cursoId));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=quadro-horario-" + cursoId + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(arquivo);
        }

        @GetMapping(value = "/exportar/planilha", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        @Operation(summary = "Exportar o quadro horário do curso em planilha")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Planilha gerada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Curso ou quadro horário ativo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Mais de um quadro horário ativo encontrado")
        })
        public ResponseEntity<byte[]> exportarPlanilha(@PathVariable("curso_id") Long cursoId) {
        byte[] arquivo = motorQuadroExcelService.exportar(motorQuadroHorario.construir(cursoId));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=quadro-horario-" + cursoId + ".xlsx")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(arquivo);
        }
}