package com.fatec.gini.domain.services.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.fatec.gini.domain.services.export.MotorQuadroExportModel.GradeData;
import com.fatec.gini.dto.grade.GradeCursoResponse;

@Service
public class MotorQuadroExcelService {

    public byte[] exportar(GradeCursoResponse grade) {
        GradeData modelo = MotorQuadroExportModel.from(grade);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Quadro horário");
            CellStyle headerStyle = criarEstiloCabecalho(workbook);
            CellStyle titleStyle = criarEstiloTitulo(workbook);
            int rowIndex = 0;

            Row title = sheet.createRow(rowIndex++);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("Quadro horário - " + modelo.grade().curso().nome());
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0,
                    0, modelo.dias().size() + 1));
            rowIndex++;

            for (MotorQuadroExportModel.TurmaData turmaData : modelo.turmas()) {
                Row turmaRow = sheet.createRow(rowIndex++);
                turmaRow.createCell(0).setCellValue(formatarTurma(turmaData));
                turmaRow.getCell(0).setCellStyle(titleStyle);

                Row header = sheet.createRow(rowIndex++);
                criarCelula(header, 0, "Horário", headerStyle);
                for (int index = 0; index < modelo.dias().size(); index++) {
                    criarCelula(header, index + 1, modelo.dias().get(index).nome(), headerStyle);
                }

                for (MotorQuadroExportModel.LinhaData linha : turmaData.linhas()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(linha.bloco().rotulo());
                    for (int index = 0; index < linha.celulas().size(); index++) {
                        row.createCell(index + 1).setCellValue(formatarCelula(linha.celulas().get(index)));
                    }
                }
                rowIndex++;
            }

            sheet.setColumnWidth(0, 24 * 256);
            for (int index = 1; index <= modelo.dias().size(); index++) {
                sheet.setColumnWidth(index, 32 * 256);
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível gerar a planilha do quadro horário", exception);
        }
    }

    private CellStyle criarEstiloCabecalho(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle criarEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    private void criarCelula(Row row, int index, String value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private String formatarTurma(MotorQuadroExportModel.TurmaData turmaData) {
        var turma = turmaData.turma();
        return "Turma " + turma.codigo() + " - " + turma.ano() + "º ano / " + turma.periodo() + "º período";
    }

    private String formatarCelula(MotorQuadroExportModel.CelulaData celula) {
        if (!celula.preenchida()) {
            return "-";
        }
        var valor = celula.valor();
        return valor.disciplina().nome() + "\n" + valor.sala().codigo();
    }
}