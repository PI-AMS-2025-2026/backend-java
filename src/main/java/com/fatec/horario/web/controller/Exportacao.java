package com.fatec.horario.web.controller;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.fatec.horario.DataHorario;
import com.fatec.horario.domain.services.PdfService;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Exportacao {
    @Autowired
    private PdfService pdfService;

    private final DataHorario dataHorario;

    Exportacao() {
        this.dataHorario = new DataHorario();
    }

    @GetMapping("/a")
    public String visualizar(Model model) {

        model.addAttribute("dataHorario", dataHorario);
        return "horario";
    }

    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> gerarPdf() throws Exception {
        ByteArrayInputStream pdf = pdfService.export(dataHorario);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=horario.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }
}
