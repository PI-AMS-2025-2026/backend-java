package com.fatec.gini.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoPeridoAtividadeQuadro;
import com.fatec.gini.domain.services.PeriodoAtividadeQuadroService;
import com.fatec.gini.domain.services.TokenService;
import com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroResponse;
import com.fatec.gini.web.config.SecurityFilter;

@WebMvcTest(controllers = {PeriodoAtividadeQuadroController.class, EnumsController.class})
@AutoConfigureMockMvc(addFilters = false)
class TipoPeriodoContratoTest {
    @Autowired MockMvc mvc;
    @MockitoBean PeriodoAtividadeQuadroService service;
    @MockitoBean SecurityFilter securityFilter;
    @MockitoBean TokenService tokenService;
    @MockitoBean AuthenticationManager authenticationManager;

    @Test
    void apiAceitaERetornaNomeCorreto() throws Exception {
        var inicio = LocalDate.of(2026, 1, 1);
        var fim = LocalDate.of(2026, 6, 30);
        when(service.criar(any())).thenAnswer(invocation -> {
            var req = invocation.getArgument(0, com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroRequest.class);
            return new PeriodoAtividadeQuadroResponse(1L, 2026, 1, req.tipoPeridoAtividadeQuadro(), inicio, fim, Status.ATIVO, LocalDateTime.now(), LocalDateTime.now());
        });
        mvc.perform(get("/periodos-atividade-quadro/tipos"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0]").value("SEMESTRAL"));
        mvc.perform(post("/periodos-atividade-quadro").contentType(MediaType.APPLICATION_JSON)
                .content("{\"ano\":2026,\"periodo\":1,\"tipo_perido_atividade_quadro\":\"SEMESTRAL\",\"data_inicio\":\"2026-01-01\",\"data_fim\":\"2026-06-30\",\"status\":\"ATIVO\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.tipo_perido_atividade_quadro").value("SEMESTRAL"));
        org.assertj.core.api.Assertions.assertThat(TipoPeridoAtividadeQuadro.SEMESTRAL.ordinal()).isZero();
        org.assertj.core.api.Assertions.assertThat(TipoPeridoAtividadeQuadro.ANUAL.ordinal()).isEqualTo(1);
    }
}
