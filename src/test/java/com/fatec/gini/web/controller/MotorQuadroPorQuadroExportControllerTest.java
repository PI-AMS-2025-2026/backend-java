package com.fatec.gini.web.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fatec.gini.domain.services.TokenService;
import com.fatec.gini.domain.services.export.MotorQuadroExcelService;
import com.fatec.gini.domain.services.export.MotorQuadroPdfService;
import com.fatec.gini.domain.services.usecase.write.MotorQuadroHorario;
import com.fatec.gini.dto.grade.GradeCursoResponse;
import com.fatec.gini.dto.grade.QuadroAnualPdf;
import com.fatec.gini.web.config.SecurityFilter;

@WebMvcTest(controllers = MotorQuadroPorQuadroController.class)
@AutoConfigureMockMvc(addFilters = false)
class MotorQuadroPorQuadroExportControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean MotorQuadroHorario motor;
    @MockitoBean MotorQuadroPdfService pdf;
    @MockitoBean MotorQuadroExcelService excel;
    @MockitoBean SecurityFilter securityFilter;
    @MockitoBean TokenService tokenService;
    @MockitoBean AuthenticationManager authenticationManager;

    @Test
    void exportaPdfAnualUsandoOQuadroSelecionadoComoOrigem() throws Exception {
        QuadroAnualPdf grade = mock(QuadroAnualPdf.class);
        when(motor.construirPdfAnual(42L)).thenReturn(grade);
        when(pdf.exportarAnual(grade)).thenReturn(new byte[]{37, 80, 68, 70});
        mvc.perform(get("/api/motor-quadro/quadros/42/exportar/pdf"))
            .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andExpect(header().string("Content-Disposition", "attachment; filename=quadro-horario-42.pdf"))
            .andExpect(content().bytes(new byte[]{37, 80, 68, 70}));
        verify(motor).construirPdfAnual(42L);
    }

    @Test
    void exportaPlanilhaDoQuadroSelecionado() throws Exception {
        GradeCursoResponse grade = mock(GradeCursoResponse.class);
        when(motor.construirPorQuadro(43L)).thenReturn(grade);
        when(excel.exportar(grade)).thenReturn(new byte[]{80, 75});
        mvc.perform(get("/api/motor-quadro/quadros/43/exportar/planilha"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .andExpect(header().string("Content-Disposition", "attachment; filename=quadro-horario-43.xlsx"));
    }

    @Test
    void recusaQuadroDeOutroCursoSemGerarArquivo() throws Exception {
        when(motor.construirPdfAnual(44L)).thenThrow(new AccessDeniedException("Sem acesso"));
        mvc.perform(get("/api/motor-quadro/quadros/44/exportar/pdf")).andExpect(status().isForbidden());
        verifyNoInteractions(pdf);
    }
}
