package com.fatec.gini.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fatec.gini.domain.services.usecase.write.MotorQuadroHorario;
import com.fatec.gini.dto.grade.GradeCursoResponse;
import com.fatec.gini.domain.services.export.MotorQuadroPdfService;
import com.fatec.gini.domain.services.export.MotorQuadroExcelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/motor-quadro/quadros/{id}")
@RequiredArgsConstructor
public class MotorQuadroPorQuadroController {
    private final MotorQuadroHorario motor;
    private final MotorQuadroPdfService pdfService;
    private final MotorQuadroExcelService excelService;

    @GetMapping
    public GradeCursoResponse obter(@PathVariable Long id) {
        return motor.construirPorQuadro(id);
    }

    @GetMapping(value = "/exportar/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exportar PDF das turmas do curso e ano do quadro",
            description = "Usa o ID do quadro para identificar o curso e o ano da turma. Reúne as turmas ativas e seus quadros ativos, separados por Período Atividade Quadro.")
    @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso", content = @Content(
            mediaType = MediaType.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary")))
    public ResponseEntity<byte[]> exportarPdf(@PathVariable Long id) {
        byte[] arquivo = pdfService.exportarAnual(motor.construirPdfAnual(id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=quadro-horario-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF).body(arquivo);
    }

    @GetMapping(value = "/exportar/planilha", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportarPlanilha(@PathVariable Long id) {
        byte[] arquivo = excelService.exportar(motor.construirPorQuadro(id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=quadro-horario-" + id + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(arquivo);
    }
}
