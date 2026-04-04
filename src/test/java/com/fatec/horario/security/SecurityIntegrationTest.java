package com.fatec.horario.security;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRetornar401SemTokenEmEndpointProtegido() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401ComTokenInvalido() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void professorPodeLerCursoComTokenValido() throws Exception {
        String tokenProfessor = loginAndGetToken("professor@fatec.local", "123456");

        mockMvc.perform(get("/curso")
                        .header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isOk());
    }

    @Test
    void professorNaoPodeCriarCurso() throws Exception {
        String tokenProfessor = loginAndGetToken("professor@fatec.local", "123456");

        mockMvc.perform(post("/curso")
                        .contentType(APPLICATION_JSON)
                        .content("{}")
                        .header("Authorization", "Bearer " + tokenProfessor))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPodeGerenciarUsuarios() throws Exception {
        String tokenAdmin = loginAndGetToken("admin@fatec.local", "123456");

        mockMvc.perform(get("/usuarios")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void bypassComHeaderDebugPermiteTestarEndpointProtegidoEmTest() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .header("X-Debug-Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    private String loginAndGetToken(String email, String senha) throws Exception {
        String requestBody = "{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}";

        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        return jsonNode.get("accessToken").asText();
    }
}
