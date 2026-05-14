package com.fatec.horario.domain.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.fatec.horario.DataHorario;


@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    public ByteArrayInputStream export(DataHorario dataHorario) throws Exception {

        Context context = new Context();
        context.setVariable("dataHorario", dataHorario);

        String html = templateEngine.process("horario", context);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.getSharedContext().setPrint(true);
        renderer.getSharedContext().setInteractive(false);
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);

        return new ByteArrayInputStream(out.toByteArray());
    }

}
