package com.fatec.gini.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.fatec.gini.domain.services.TokenService;
import com.fatec.gini.domain.services.UsuarioService;
import com.fatec.gini.web.config.SecurityFilter;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService service;

    @MockitoBean
    private SecurityFilter securityFilter;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    void deveRetornarErrosDeValidacaoAssociadosAosCampos() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.field_errors[?(@.field == 'nome')].message").value("Nome é obrigatório."))
                .andExpect(jsonPath("$.field_errors[?(@.field == 'email')].message").value("E-mail é obrigatório."))
                .andExpect(jsonPath("$.field_errors[?(@.field == 'status')].message").value("Status é obrigatório."))
                .andExpect(jsonPath("$.field_errors[?(@.field == 'tipoUsuario')].message").value("Tipo de usuário é obrigatório."));
    }

    @Test
    void deveRetornarRazaoQuandoCursoDoCoordenadorNaoExiste() throws Exception {
        doThrow(new EntityNotFoundException("Curso não encontrado com ID: 999"))
                .when(service).criar(any());

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Coordenação\",\"email\":\"coord@teste.com\",\"senha\":\"Senha123\",\"status\":\"ATIVO\",\"tipo_usuario\":\"COORDENADOR\",\"curso\":{\"id\":999}}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Curso não encontrado com ID: 999"));
    }

    @Test
    void deveRejeitarSenhaFracaAoCriarUsuario() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"email\":\"ana@teste.com\",\"senha\":\"abcdefgh\",\"status\":\"ATIVO\",\"tipo_usuario\":\"ADMINISTRADOR\"}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.field_errors[?(@.field == 'senha')].message")
                        .value("A senha deve ter pelo menos 8 caracteres, uma letra maiúscula e uma letra minúscula."));
    }

    @Test
    void deveRejeitarSenhaFracaAoAtualizarUsuario() throws Exception {
        mockMvc.perform(put("/usuarios/31")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"email\":\"ana@teste.com\",\"senha\":\"ABCDEFGH\",\"status\":\"ATIVO\",\"tipo_usuario\":\"ADMINISTRADOR\"}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.field_errors[?(@.field == 'senha')].message")
                        .value("A senha deve ter pelo menos 8 caracteres, uma letra maiúscula e uma letra minúscula."));
    }

    @Test
    void deveManterDeleteComoOperacaoDeInativacao() throws Exception {
        doNothing().when(service).inativar(12L);

        mockMvc.perform(delete("/usuarios/12"))
                .andExpect(status().isNoContent());

        verify(service).inativar(12L);
    }
}
