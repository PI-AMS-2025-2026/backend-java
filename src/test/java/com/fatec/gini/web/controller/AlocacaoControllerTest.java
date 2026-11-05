package com.fatec.gini.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fatec.gini.domain.services.AlocacaoService;
import com.fatec.gini.domain.services.TokenService;
import com.fatec.gini.dto.alocacao.ValidarCargaHorariaRequest;
import com.fatec.gini.dto.alocacao.ValidarCargaHorariaResponse;
import com.fatec.gini.web.config.SecurityFilter;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(controllers = AlocacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AlocacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlocacaoService service;

    @MockitoBean
    private SecurityFilter securityFilter;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    void deveRetornar200ComValidoTrueQuandoCargaHorariaForValida() throws Exception {
        when(service.validarCargaHorariaSemPersistir(any(ValidarCargaHorariaRequest.class)))
                .thenReturn(new ValidarCargaHorariaResponse(true,
                        "Carga horária válida para o professor no dia informado."));

        mockMvc.perform(post("/alocacoes/validar-carga-horaria")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "professor": { "id": 5 },
                            "dia_semana": "SEGUNDA",
                            "horario": { "id": 1 }
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true))
                .andExpect(jsonPath("$.mensagem").value("Carga horária válida para o professor no dia informado."));
    }

    @Test
    void deveRetornar200ComValidoFalseQuandoCargaHorariaForInvalidaPelaRegra() throws Exception {
        when(service.validarCargaHorariaSemPersistir(any(ValidarCargaHorariaRequest.class)))
                .thenReturn(new ValidarCargaHorariaResponse(false,
                        "A carga horária máxima diária do professor foi excedida."));

        mockMvc.perform(post("/alocacoes/validar-carga-horaria")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "professor": { "id": 5 },
                            "dia_semana": "SEGUNDA",
                            "horario": { "id": 1 }
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false))
                .andExpect(jsonPath("$.mensagem").value("A carga horária máxima diária do professor foi excedida."));
    }

    @Test
    void deveRetornar404QuandoProfessorNaoEncontrado() throws Exception {
        when(service.validarCargaHorariaSemPersistir(any(ValidarCargaHorariaRequest.class)))
                .thenThrow(new EntityNotFoundException("Professor não encontrado com ID: 99"));

        mockMvc.perform(post("/alocacoes/validar-carga-horaria")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "professor": { "id": 99 },
                            "dia_semana": "SEGUNDA",
                            "horario": { "id": 1 }
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Professor não encontrado com ID: 99"));
    }

    @Test
    void deveRetornar404QuandoBlocoHorarioNaoEncontrado() throws Exception {
        when(service.validarCargaHorariaSemPersistir(any(ValidarCargaHorariaRequest.class)))
                .thenThrow(new EntityNotFoundException("Bloco horário não encontrado com ID: 99"));

        mockMvc.perform(post("/alocacoes/validar-carga-horaria")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "professor": { "id": 5 },
                            "dia_semana": "SEGUNDA",
                            "horario": { "id": 99 }
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Bloco horário não encontrado com ID: 99"));
    }

    @Test
    void deveRetornar422QuandoProfessorForNulo() throws Exception {
        mockMvc.perform(post("/alocacoes/validar-carga-horaria")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "professor": null,
                            "dia_semana": "SEGUNDA",
                            "horario": { "id": 1 }
                        }
                        """))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void deveRetornar400QuandoDiaSemanaForInvalido() throws Exception {
        mockMvc.perform(post("/alocacoes/validar-carga-horaria")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "professor": { "id": 5 },
                            "dia_semana": "INVALIDO",
                            "horario": { "id": 1 }
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar422QuandoHorarioForNulo() throws Exception {
        mockMvc.perform(post("/alocacoes/validar-carga-horaria")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "professor": { "id": 5 },
                            "dia_semana": "SEGUNDA",
                            "horario": null
                        }
                        """))
                .andExpect(status().isUnprocessableContent());
    }
}
