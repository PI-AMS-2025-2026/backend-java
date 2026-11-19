package com.fatec.gini.domain.services.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fatec.gini.dto.grade.GradeCursoResponse;
import com.fatec.gini.dto.grade.QuadroAnualPdf;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MotorQuadroPdfService {

    private final SpringTemplateEngine templateEngine;

    public byte[] exportar(GradeCursoResponse grade) {
        return renderizar(MotorQuadroExportModel.documentoPdf(grade));
    }

    public byte[] exportarAnual(QuadroAnualPdf grade) {
        return renderizar(MotorQuadroExportModel.documentoPdf(grade));
    }

    private byte[] renderizar(MotorQuadroExportModel.PdfData modelo) {
        Context contexto = new Context();
        contexto.setVariable("modelo", modelo);
        String html = templateEngine.process("QuadroHorario", contexto);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            new PdfRendererBuilder()
                    .useFastMode()
                    .withHtmlContent(html, null)
                    .toStream(output)
                    .run();
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível gerar o PDF do quadro horário", exception);
        }
    }
}
