package com.fatec.horario.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "app.security.debug-role-enabled=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityDebugRoleDisabledTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void headerDebugNaoDeveAutenticarQuandoFeatureFlagDesativada() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .header("X-Debug-Role", "ADMIN"))
                .andExpect(status().isUnauthorized());
    }
}
