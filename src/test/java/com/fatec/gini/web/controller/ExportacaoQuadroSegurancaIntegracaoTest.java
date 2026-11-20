package com.fatec.gini.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fatec.gini.domain.entities.RefreshToken;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoPeridoAtividadeQuadro;
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
    @Autowired PeriodoAtividadeQuadroRepository periodos;
    @Autowired AlocacaoRepository alocacoes;
    @Autowired BlocoHorarioRepository blocos;
    @Autowired DisciplinaRepository disciplinas;
    @Autowired ProfessorRepository professores;
    @Autowired SalaRepository salas;
    @Value("${api.security.token.secret}") String jwtSecret;
    @Value("${api.security.token.issuer}") String jwtIssuer;

    @Test
    void tokenAusenteOuExpiradoRetorna401EMantem403ParaFaltaDePermissao() throws Exception {
        mvc.perform(get("/cursos")).andExpect(status().isUnauthorized());

        String tokenExpirado = JWT.create()
                .withIssuer(jwtIssuer)
                .withSubject("expirado@teste.local")
                .withExpiresAt(Instant.now().minusSeconds(60))
                .sign(Algorithm.HMAC256(jwtSecret));
        mvc.perform(get("/cursos").header("Authorization", "Bearer " + tokenExpirado))
                .andExpect(status().isUnauthorized());

        var curso = cursos.findAll().get(0);
        var coordenador = new Usuario("Coordenador", "permissao@teste.local", "senha", Status.ATIVO,
                TipoUsuario.COORDENADOR);
        coordenador.setCurso(curso);
        usuarios.saveAndFlush(coordenador);
        mvc.perform(get("/usuarios").header("Authorization", "Bearer " + token(coordenador, "sessao-permissao")))
                .andExpect(status().isForbidden());
    }

    @Test
    void coordenadorEAdministradorPodemCriarPeriodo() throws Exception {
        var curso = cursos.findAll().get(0);
        var coordenador = new Usuario("Coordenador de períodos", "periodo-coord@teste.local", "senha",
                Status.ATIVO, TipoUsuario.COORDENADOR);
        coordenador.setCurso(curso);
        usuarios.saveAndFlush(coordenador);

        String corpo = "{\"ano\":2027,\"periodo\":1,\"tipo_perido_atividade_quadro\":\"SEMESTRAL\"," +
                "\"data_inicio\":\"2027-02-01\",\"data_fim\":\"2027-06-30\",\"status\":\"ATIVO\"}";
        mvc.perform(post("/periodos-atividade-quadro")
                .header("Authorization", "Bearer " + token(coordenador, "sessao-periodo-coord"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo))
                .andExpect(status().isCreated());

        var administrador = new Usuario("Administrador", "periodo-admin@teste.local", "senha", Status.ATIVO,
                TipoUsuario.ADMINISTRADOR);
        usuarios.saveAndFlush(administrador);
        String segundoPeriodo = "{\"ano\":2027,\"periodo\":2,\"tipo_perido_atividade_quadro\":\"SEMESTRAL\"," +
                "\"data_inicio\":\"2027-08-01\",\"data_fim\":\"2027-12-20\",\"status\":\"ATIVO\"}";
        mvc.perform(post("/periodos-atividade-quadro")
                .header("Authorization", "Bearer " + token(administrador, "sessao-periodo-admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(segundoPeriodo))
                .andExpect(status().isCreated());
    }

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
        var resultadoFrontend = mvc.perform(get(rota).header("Authorization", "Bearer " + token).accept(MediaType.ALL))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn().getResponse().getContentAsByteArray();
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/motor-quadro/quadros/{id}/exportar/pdf'].get.responses['200']"
                        + ".content['application/pdf'].schema.format").value("binary"));
        var resultadoSwagger = mvc.perform(get(rota).header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn().getResponse().getContentAsByteArray();
        try (var pdfFrontend = PDDocument.load(new ByteArrayInputStream(resultadoFrontend));
                var pdfSwagger = PDDocument.load(new ByteArrayInputStream(resultadoSwagger))) {
            var leitor = new PDFTextStripper();
            String textoFrontend = leitor.getText(pdfFrontend);
            assertThat(textoFrontend).contains(turma.getCurso().getNome(), "Ano das turmas: " + turma.getAno(),
                    "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado");
            assertThat(leitor.getText(pdfSwagger)).isEqualTo(textoFrontend);
        }
        var outroCurso = cursos.findAll().stream().filter(c -> !c.getId().equals(turma.getCurso().getId())).findFirst().orElseThrow();
        var outro = new Usuario("Outro coordenador", "exportacao-outro@teste.local", "senha", Status.ATIVO, TipoUsuario.COORDENADOR);
        outro.setCurso(outroCurso);
        usuarios.saveAndFlush(outro);
        mvc.perform(get(rota).header("Authorization", "Bearer " + token(outro, "sessao-outro")))
                .andExpect(status().isForbidden());
    }

    @Test
    void consolidaTurmasAtivasDoMesmoAnoSemMisturarHorariosDosPeriodos() throws Exception {
        var curso = cursos.save(new Curso("ADS Exportação anual", "Anual", Status.ATIVO, 4));
        var outroCurso = cursos.save(new Curso("Outro curso para PDF", "Anual", Status.ATIVO, 2));
        var professor = professores.findAll().get(0);
        var sala = salas.findAll().get(0);
        var manha = blocos.save(new BlocoHorario(LocalTime.of(7, 1), LocalTime.of(7, 51), 50));
        var noite = blocos.save(new BlocoHorario(LocalTime.of(19, 1), LocalTime.of(19, 51), 50));
        var outraNoite = blocos.save(new BlocoHorario(LocalTime.of(20, 1), LocalTime.of(20, 51), 50));
        blocos.save(new BlocoHorario(LocalTime.of(11, 1), LocalTime.of(11, 51), 50));

        var anual = periodoPdf(2028, 1, TipoPeridoAtividadeQuadro.ANUAL, 1, 12);
        var primeiroSemestre = periodoPdf(2028, 1, TipoPeridoAtividadeQuadro.SEMESTRAL, 1, 6);
        var segundoSemestre = periodoPdf(2028, 2, TipoPeridoAtividadeQuadro.SEMESTRAL, 7, 12);
        var primeira = turmaPdf(curso, 2028, 1, Status.ATIVO);
        var segunda = turmaPdf(curso, 2028, 2, Status.ATIVO);
        turmaPdf(curso, 2028, 3, Status.ATIVO);
        var inativa = turmaPdf(curso, 2028, 4, Status.INATIVO);
        var outroAno = turmaPdf(curso, 2029, 1, Status.ATIVO);
        var turmaOutroCurso = turmaPdf(outroCurso, 2028, 1, Status.ATIVO);

        var q1 = quadroPdf(primeira, anual, 1, Status.ATIVO);
        var q2 = quadroPdf(segunda, primeiroSemestre, 2, Status.ATIVO);
        var q3 = quadroPdf(segunda, segundoSemestre, 1, Status.ATIVO);
        var historico = quadroPdf(segunda, primeiroSemestre, 1, Status.INATIVO);
        var qInativa = quadroPdf(inativa, anual, 1, Status.ATIVO);
        var qOutroAno = quadroPdf(outroAno, anual, 1, Status.ATIVO);
        var qOutroCurso = quadroPdf(turmaOutroCurso, anual, 1, Status.ATIVO);
        var quadrosTeste = java.util.List.of(q1, q2, q3, historico, qInativa, qOutroAno, qOutroCurso);
        var horarios = java.util.List.of(manha, noite, outraNoite, manha, manha, manha, manha);
        var nomes = java.util.List.of("Aula Matutina", "Aula Noturna A", "Aula Noturna B", "Aula Histórica",
                "Aula Turma Inativa", "Aula Outro Ano", "Aula Outro Curso");
        for (int i = 0; i < quadrosTeste.size(); i++) {
            var quadro = quadrosTeste.get(i);
            var disciplina = new Disciplina(nomes.get(i), 40, "Teórica", quadro.getTurma().getPeriodo(),
                    "Presencial", "PDFANUAL" + i, null);
            disciplina.setCurso(quadro.getCurso());
            disciplina.setTipoSala(sala.getTipoSala());
            alocacoes.save(new Alocacao(quadro.getTurma(), disciplinas.save(disciplina), sala, professor,
                    DiaSemana.SEGUNDA, horarios.get(i), quadro));
        }
        var coordenador = new Usuario("Coordenador PDF anual", "pdf-anual@teste.local", "senha",
                Status.ATIVO, TipoUsuario.COORDENADOR);
        coordenador.setCurso(curso);
        usuarios.saveAndFlush(coordenador);
        String token = token(coordenador, "sessao-pdf-anual");

        String texto = textoPdfAnual(q2.getId(), token);
        assertThat(texto).contains("ADS Exportação anual", "Ano das turmas: 2028",
                "Turma 3/2028", "Nenhum quadro ativo para esta turma.")
                .doesNotContain("Turma 4/2028", "Turma 1/2029", "Aula Histórica", "Aula Turma Inativa",
                        "Aula Outro Ano", "Aula Outro Curso", "11h01-11h51");
        assertThat(texto).containsSubsequence("Turma 1/2028", "Período anual de 2028", "07h01-07h51",
                "Aula Matutina", "Turma 2/2028", "1º semestre de 2028", "19h01-19h51", "Aula Noturna A",
                "2º semestre de 2028", "20h01-20h51", "Aula Noturna B", "Turma 3/2028");
        assertThat(texto.substring(texto.indexOf("Turma 1/2028"), texto.indexOf("Turma 2/2028")))
                .doesNotContain("19h01-19h51", "20h01-20h51");
        assertThat(texto.substring(texto.indexOf("Turma 2/2028"))).doesNotContain("07h01-07h51");
        assertThat(texto.split("Sábado", -1)).hasSize(4);
        assertThat(textoPdfAnual(q1.getId(), token)).isEqualTo(texto);
    }

    private String textoPdfAnual(Long quadroId, String token) throws Exception {
        var resultado = mvc.perform(get("/motor-quadro/quadros/" + quadroId + "/exportar/pdf")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn().getResponse().getContentAsByteArray();
        try (var pdf = PDDocument.load(resultado)) {
            return new PDFTextStripper().getText(pdf);
        }
    }

    private PeriodoAtividadeQuadro periodoPdf(int ano, int numero, TipoPeridoAtividadeQuadro tipo,
            int inicio, int fim) {
        var periodo = new PeriodoAtividadeQuadro(ano, numero, LocalDate.of(ano, inicio, 1),
                LocalDate.of(ano, fim, 20), Status.ATIVO);
        periodo.setTipoPeridoAtividadeQuadro(tipo);
        return periodos.save(periodo);
    }

    private Turma turmaPdf(Curso curso, int ano, int etapa, Status status) {
        var turma = new Turma(etapa + "/" + ano, etapa, ano, 20);
        turma.setCurso(curso);
        turma.setStatus(status);
        return turmas.save(turma);
    }

    private QuadroHorario quadroPdf(Turma turma, PeriodoAtividadeQuadro periodo, int versao, Status status) {
        var quadro = new QuadroHorario(versao, status);
        quadro.setCurso(turma.getCurso());
        quadro.setTurma(turma);
        quadro.setPeriodoAtividadeQuadro(periodo);
        quadro.setDataCriacao(LocalDateTime.now());
        return quadros.save(quadro);
    }

    private String token(Usuario usuario, String sessao) {
        var refresh = new RefreshToken();
        refresh.setUsuario(usuario); refresh.setToken(sessao); refresh.setExpiryDate(Instant.now().plusSeconds(3600));
        sessoes.saveAndFlush(refresh);
        return tokens.gerarToken(usuario, sessao);
    }
}
