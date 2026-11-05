package com.fatec.gini.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fatec.gini.domain.entities.RefreshToken;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;
import com.fatec.gini.domain.services.PasswordResetService;
import com.fatec.gini.domain.services.RefreshTokenService;
import com.fatec.gini.domain.services.TokenService;
import com.fatec.gini.web.config.SecurityFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AutenticacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void deveAutenticarEDevolverTokensEmSnakeCase() throws Exception {
        Usuario usuario = new Usuario("Admin", "admin@gini.local", "senha", Status.ATIVO,
                TipoUsuario.ADMINISTRADOR);
        usuario.setId(1L);
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-123");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(refreshToken);
        when(tokenService.gerarToken(usuario, "refresh-123")).thenReturn("access-123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@gini.local\",\"senha\":\"senha\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-123"))
                .andExpect(jsonPath("$.refresh_token").value("refresh-123"));
    }

    @Test
    void deveAceitarSolicitacaoDeRecuperacaoSemExporExistenciaDoEmail() throws Exception {
        doNothing().when(passwordResetService).solicitarRecuperacao("desconhecido@gini.local");

        mockMvc.perform(post("/auth/solicitar-recuperacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"desconhecido@gini.local\"}"))
                .andExpect(status().isAccepted());

        verify(passwordResetService).solicitarRecuperacao("desconhecido@gini.local");
    }

    @Test
    void deveRedefinirSenhaEDevolverNoContent() throws Exception {
        doNothing().when(passwordResetService).redefinirSenha("token-123", "nova-senha");

        mockMvc.perform(post("/auth/redefinir-senha")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"token-123\",\"senha\":\"nova-senha\"}"))
                .andExpect(status().isNoContent());

        verify(passwordResetService).redefinirSenha("token-123", "nova-senha");
    }

    @Test
    void deveRejeitarSolicitacaoDeRecuperacaoComEmailInvalido() throws Exception {
        mockMvc.perform(post("/auth/solicitar-recuperacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"invalido\"}"))
                .andExpect(status().isUnprocessableContent());
    }
}