package com.fatec.gini.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fatec.gini.domain.entities.RefreshToken;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;
import com.fatec.gini.domain.services.TokenService;
import com.fatec.gini.infrastructure.repositories.*;

@SpringBootTest(properties = "app.security.authorization.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExportacaoQuadroSegurancaIntegracaoTest {
    @Autowired MockMvc mvc;
    @Autowired TurmaRepository turmas;
    @Autowired QuadroHorarioRepository quadros;
    @Autowired CursoRepository cursos;
    @Autowired UsuarioRepository usuarios;
    @Autowired RefreshTokenRepository sessoes;
    @Autowired TokenService tokens;

    @Test
    void coordenadorCriaQuadroDoSeuCursoERecusaTurmaDeOutroCurso() throws Exception {
        var turma = turmas.findAll().get(0);
        var periodo = quadros.findAll().stream().filter(q -> q.getCurso().getId().equals(turma.getCurso().getId()))
                .findFirst().orElseThrow().getPeriodoAtividadeQuadro();
        var outra = turmas.findAll().stream().filter(t -> !t.getCurso().getId().equals(turma.getCurso().getId()))
                .findFirst().orElseThrow();
        var coordenador = new Usuario("Coordenação", "jornada@teste.local", "senha", Status.ATIVO, TipoUsuario.COORDENADOR);
        coordenador.setCurso(turma.getCurso());
        usuarios.saveAndFlush(coordenador);
        String credencial = token(coordenador, "sessao-jornada");
        String corpo = "{\"versao\":1,\"status\":\"ATIVO\",\"curso\":{\"id\":" + turma.getCurso().getId()
                + "},\"turma\":{\"id\":" + turma.getId() + "},\"periodo_atividade_quadro\":{\"id\":" + periodo.getId() + "}}";
        mvc.perform(post("/quadros-horarios").header("Authorization", "Bearer " + credencial)
                .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.turma.id").value(turma.getId()))
                .andExpect(jsonPath("$.versao").value(1));
        String corpoOutro = "{\"versao\":1,\"status\":\"ATIVO\",\"curso\":{\"id\":" + turma.getCurso().getId()
                + "},\"turma\":{\"id\":" + outra.getId() + "},\"periodo_atividade_quadro\":{\"id\":" + periodo.getId() + "}}";
        mvc.perform(post("/quadros-horarios").header("Authorization", "Bearer " + credencial)
                .contentType(MediaType.APPLICATION_JSON).content(corpoOutro)).andExpect(status().isForbidden());
    }

    @Test
    void autenticaEAutorizaCursoAntesDeGerarPdfLegivel() throws Exception {
        var turma = turmas.findAll().get(0);
        var quadro = quadros.findAll().stream().filter(q -> q.getCurso().getId().equals(turma.getCurso().getId()))
                .findFirst().orElseThrow();
        quadro.setTurma(turma);
        quadros.saveAndFlush(quadro);
        String rota = "/motor-quadro/quadros/" + quadro.getId() + "/exportar/pdf";
        int semToken = mvc.perform(get(rota)).andReturn().getResponse().getStatus();
        assertThat(semToken).isIn(401, 403);

        var coordenador = new Usuario("Coordenador teste", "exportacao@teste.local", "senha", Status.ATIVO, TipoUsuario.COORDENADOR);
        coordenador.setCurso(turma.getCurso());
        usuarios.saveAndFlush(coordenador);
        String token = token(coordenador, "sessao-exportacao");
        var resultado = mvc.perform(get(rota).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn().getResponse().getContentAsByteArray();
        try (var pdf = PDDocument.load(new ByteArrayInputStream(resultado))) {
            assertThat(new PDFTextStripper().getText(pdf)).contains(turma.getCurso().getNome());
        }
        var outroCurso = cursos.findAll().stream().filter(c -> !c.getId().equals(turma.getCurso().getId())).findFirst().orElseThrow();
        var outro = new Usuario("Outro coordenador", "exportacao-outro@teste.local", "senha", Status.ATIVO, TipoUsuario.COORDENADOR);
        outro.setCurso(outroCurso);
        usuarios.saveAndFlush(outro);
        mvc.perform(get(rota).header("Authorization", "Bearer " + token(outro, "sessao-outro")))
                .andExpect(status().isForbidden());
    }

    private String token(Usuario usuario, String sessao) {
        var refresh = new RefreshToken();
        refresh.setUsuario(usuario); refresh.setToken(sessao); refresh.setExpiryDate(Instant.now().plusSeconds(3600));
        sessoes.saveAndFlush(refresh);
        return tokens.gerarToken(usuario, sessao);
    }
}
