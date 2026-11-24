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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fatec.gini.domain.entities.RefreshToken;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;
import com.fatec.gini.domain.services.PasswordResetService;
import com.fatec.gini.domain.services.RefreshTokenService;
import com.fatec.gini.domain.services.TokenService;
import com.fatec.gini.dto.auth.LoginResponse;
import com.fatec.gini.web.exception.RefreshTokenException;
import com.fatec.gini.web.config.SecurityFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    void deveRenovarTokensEmSnakeCase() throws Exception {
        when(refreshTokenService.refresh("refresh-123"))
                .thenReturn(new LoginResponse("access-456", "refresh-789", null));

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refresh_token\":\"refresh-123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-456"))
                .andExpect(jsonPath("$.refresh_token").value("refresh-789"));

        verify(refreshTokenService).refresh("refresh-123");
    }

    @Test
    void deveDevolver401QuandoRefreshTokenExpirar() throws Exception {
        when(refreshTokenService.refresh("refresh-expirado"))
                .thenThrow(new RefreshTokenException("Refresh token inválido ou expirado"));

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refresh_token\":\"refresh-expirado\"}"))
                .andExpect(status().isUnauthorized());
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
        doNothing().when(passwordResetService).redefinirSenha("token-123", "Nova-senha");

        mockMvc.perform(post("/auth/redefinir-senha")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"token-123\",\"senha\":\"Nova-senha\"}"))
                .andExpect(status().isNoContent());

        verify(passwordResetService).redefinirSenha("token-123", "Nova-senha");
    }

    @Test
    void deveRejeitarSenhaFracaNaRedefinicao() throws Exception {
        mockMvc.perform(post("/auth/redefinir-senha")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"token-123\",\"senha\":\"senha-fraca\"}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.field_errors[?(@.field == 'senha')].message")
                        .value("A senha deve ter pelo menos 8 caracteres, uma letra maiúscula e uma letra minúscula."));
    }

    @Test
    void deveRejeitarSenhaFracaNaAlteracao() throws Exception {
        mockMvc.perform(put("/auth/alterar-senha")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"senha_atual\":\"Senha-atual\",\"nova_senha\":\"senha-fraca\"}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.field_errors[?(@.field == 'novaSenha')].message")
                        .value("A senha deve ter pelo menos 8 caracteres, uma letra maiúscula e uma letra minúscula."));
    }

    @Test
    void deveAlterarSenhaDoUsuarioAutenticado() throws Exception {
        Usuario usuario = new Usuario("Admin", "admin@gini.local", "hash-atual", Status.ATIVO,
                TipoUsuario.ADMINISTRADOR);
        usuario.setId(1L);
        doNothing().when(passwordResetService).alterarSenha(usuario, "Senha-atual", "Nova-senha");

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
        SecurityContextHolder.setContext(context);
        try {
            mockMvc.perform(put("/auth/alterar-senha")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"senha_atual\":\"Senha-atual\",\"nova_senha\":\"Nova-senha\"}"))
                    .andExpect(status().isNoContent());

            verify(passwordResetService).alterarSenha(usuario, "Senha-atual", "Nova-senha");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void deveRejeitarSolicitacaoDeRecuperacaoComEmailInvalido() throws Exception {
        mockMvc.perform(post("/auth/solicitar-recuperacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"invalido\"}"))
                .andExpect(status().isUnprocessableContent());
    }
}
