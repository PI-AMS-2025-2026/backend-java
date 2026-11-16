package com.fatec.gini.domain.services.export;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.dto.grade.CursoGradeResumoResponse;
import com.fatec.gini.dto.grade.DisciplinaGradeResumoResponse;
import com.fatec.gini.dto.grade.GradeCursoResponse;
import com.fatec.gini.dto.grade.QuadroHorarioGradeResumoResponse;
import com.fatec.gini.dto.grade.SalaGradeResumoResponse;
import com.fatec.gini.dto.quadroHorario.BlocoQuadroResponse;
import com.fatec.gini.dto.quadroHorario.CelulaGradeResponse;
import com.fatec.gini.dto.quadroHorario.TurmaQuadroResponse;

class MotorQuadroExportTest {

    @Test
    void deveExportarSomenteBlocosReaisDeCadaTurma() {
        GradeCursoResponse grade = gradeComTurnoNoturnoECargaParcial();

        MotorQuadroExportModel.GradeData modelo = MotorQuadroExportModel.from(grade);

        assertEquals(List.of("19h00-20h40", "20h50-22h30"),
                modelo.turmas().get(0).linhas().stream()
                        .map(linha -> linha.bloco().rotulo())
                        .toList());
        assertEquals(List.of("19h00-20h40"),
                modelo.turmas().get(1).linhas().stream()
                        .map(linha -> linha.bloco().rotulo())
                        .toList());
    }

    @Test
    void deveManterLacunaSemCriarLinhaDeHorario() {
        GradeCursoResponse grade = gradeComTurnoNoturnoECargaParcial();

        MotorQuadroExportModel.TurmaData turma = MotorQuadroExportModel.from(grade).turmas().get(0);

        assertEquals(List.of("19h00-20h40", "20h50-22h30"),
                turma.linhas().stream().map(linha -> linha.bloco().rotulo()).toList());
        assertFalse(turma.linhas().stream().anyMatch(linha -> linha.bloco().rotulo().equals("18h00-18h50")));
    }

    @Test
    void deveGerarXlsxValidoComQuatroAulasEConjuntosVariaveis() throws IOException {
        GradeCursoResponse grade = gradeComQuatroAulas();

        byte[] arquivo = new MotorQuadroExcelService().exportar(grade);

        try (var workbook = WorkbookFactory.create(new ByteArrayInputStream(arquivo))) {
            var sheet = workbook.getSheetAt(0);
            assertEquals("Quadro horário", sheet.getSheetName());
            assertEquals("19h00-20h40", sheet.getRow(4).getCell(0).getStringCellValue());
            assertEquals("20h50-22h30", sheet.getRow(5).getCell(0).getStringCellValue());
            assertEquals("22h40-23h30", sheet.getRow(6).getCell(0).getStringCellValue());
            assertEquals("23h40-00h30", sheet.getRow(7).getCell(0).getStringCellValue());
            assertTrue(sheet.getRow(4).getCell(1).getStringCellValue().contains("Disciplina 1"));
        }
    }

    @Test
    void deveGerarPdfValidoSemInventarLinhas() {
        GradeCursoResponse grade = gradeComTurnoNoturnoECargaParcial();
        MotorQuadroPdfService service = new MotorQuadroPdfService(templateEngine());

        byte[] arquivo = service.exportar(grade);

        assertTrue(arquivo.length > 4);
        assertArrayEquals(new byte[] { 0x25, 0x50, 0x44, 0x46 }, new byte[] {
                arquivo[0], arquivo[1], arquivo[2], arquivo[3]
        });
    }

    private SpringTemplateEngine templateEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }

    private GradeCursoResponse gradeComTurnoNoturnoECargaParcial() {
        BlocoQuadroResponse intervalo = bloco(2L, "18h00-18h50", 18, 0, 18, 50);
        BlocoQuadroResponse primeiro = bloco(1L, "19h00-20h40", 19, 0, 20, 40);
        BlocoQuadroResponse segundo = bloco(3L, "20h50-22h30", 20, 50, 22, 30);
        CelulaGradeResponse aulaPrimeira = celula(DiaSemana.SEGUNDA, 1L, "Disciplina noturna 1", "S1");
        CelulaGradeResponse aulaSegunda = celula(DiaSemana.QUARTA, 3L, "Disciplina noturna 2", "S2");

        return grade(List.of(intervalo, primeiro, segundo),
                List.of(turma(1L, List.of(aulaPrimeira, aulaSegunda)), turma(2L,
                        List.of(celula(DiaSemana.TERÇA, 1L, "Disciplina parcial", "S3")))));
    }

    private GradeCursoResponse gradeComQuatroAulas() {
        List<BlocoQuadroResponse> blocos = List.of(
                bloco(1L, "19h00-20h40", 19, 0, 20, 40),
                bloco(2L, "20h50-22h30", 20, 50, 22, 30),
                bloco(3L, "22h40-23h30", 22, 40, 23, 30),
                bloco(4L, "23h40-00h30", 23, 40, 0, 30),
                bloco(5L, "00h40-01h30", 0, 40, 1, 30));
        List<CelulaGradeResponse> aulas = List.of(
                celula(DiaSemana.SEGUNDA, 1L, "Disciplina 1", "S1"),
                celula(DiaSemana.TERÇA, 2L, "Disciplina 2", "S2"),
                celula(DiaSemana.QUARTA, 3L, "Disciplina 3", "S3"),
                celula(DiaSemana.QUINTA, 4L, "Disciplina 4", "S4"));
        return grade(blocos, List.of(turma(1L, aulas)));
    }

    private GradeCursoResponse grade(List<BlocoQuadroResponse> blocos, List<TurmaQuadroResponse> turmas) {
        return new GradeCursoResponse(
                new CursoGradeResumoResponse(1L, "Sistemas de Informação", "Anual"),
                new QuadroHorarioGradeResumoResponse(1L, 1, Status.ATIVO), blocos, turmas);
    }

    private TurmaQuadroResponse turma(Long id, List<CelulaGradeResponse> celulas) {
        return new TurmaQuadroResponse(id, id + "/2026", 2026, id.intValue(), celulas);
    }

    private CelulaGradeResponse celula(DiaSemana dia, Long blocoId, String disciplina, String sala) {
        return new CelulaGradeResponse(dia, blocoId,
                new DisciplinaGradeResumoResponse(blocoId, disciplina),
                new SalaGradeResumoResponse(blocoId, sala), blocoId);
    }

    private BlocoQuadroResponse bloco(Long id, String rotulo, int inicioHora, int inicioMinuto,
            int fimHora, int fimMinuto) {
        return new BlocoQuadroResponse(id, LocalTime.of(inicioHora, inicioMinuto),
                LocalTime.of(fimHora, fimMinuto), rotulo);
    }
}
