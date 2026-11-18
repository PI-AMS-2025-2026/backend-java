package com.fatec.gini.infrastructure.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import com.fatec.gini.domain.entities.BlocoHorario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.TipoSala;
import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoPeridoAtividadeQuadro;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = { "spring.flyway.enabled=true", "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.defer-datasource-initialization=false", "spring.sql.init.mode=never",
        "app.security.authorization.enabled=false" })
@ActiveProfiles("test")
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(addFilters = false)
@EnabledIfEnvironmentVariable(named = "QUADRO_TEST_PG_URL", matches = ".+")
@Transactional
class QuadroFiltrosPostgresIntegracaoTest {
    @Autowired
    QuadroHorarioRepository quadros;
    @Autowired
    PeriodoAtividadeQuadroRepository periodos;
    @Autowired
    EntityManager entityManager;
    @Autowired
    BlocoHorarioRepository blocos;
    @Autowired
    org.springframework.test.web.servlet.MockMvc mvc;

    @Test
    void disciplinaCrudComPostgresEValidacoes() throws Exception {
        var curso = new Curso("Curso integração disciplina", "Semestral", Status.ATIVO, 6);
        entityManager.persist(curso);
        var tipo = new TipoSala("Tipo integração disciplina");
        entityManager.persist(tipo);
        entityManager.flush();
        var payload = """
                {"nome":"Disciplina integração","carga_horaria":40,"tipo_disciplina":"Teórica","periodo":4,
                 "modalidade":"Presencial","cod_disciplina":"BD02-INT","cor":"#f5a623",
                 "curso":{"id":%d},"tipo_sala":{"id":%d}}
                """.formatted(curso.getId(), tipo.getId());
        var get = org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/disciplinas").param("page", "0").param("size", "100");
        mvc.perform(get).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        var created = mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/disciplinas")
                .contentType("application/json").content(payload))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.cod_disciplina").value("BD02-INT"))
                .andReturn();
        var id = new com.fasterxml.jackson.databind.ObjectMapper().readTree(created.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.content[0].periodo").value(4));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/disciplinas/{id}", id)
                .contentType("application/json").content(payload.replace("Disciplina integração", "Disciplina editada")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.nome").value("Disciplina editada"));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/disciplinas")
                .contentType("application/json").content(payload))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isConflict());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/disciplinas")
                .contentType("application/json").content(payload.replace("BD02-INT", "BD03-INT").replace("\"carga_horaria\":40", "\"carga_horaria\":19")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnprocessableContent());
    }

    @Test
    void endpointBlocosListaSemFiltrosNoPostgres() throws Exception {
        blocos.saveAndFlush(new BlocoHorario(LocalTime.of(7, 40), LocalTime.of(8, 30), 50));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/bloco-horarios")
                .param("page", "0").param("size", "6"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.content[0].hora_inicio").value("07:40:00"));
    }

    @Test
    void listaBlocosComFiltrosOpcionaisEPaginacao() {
        var primeiro = blocos.saveAndFlush(new BlocoHorario(LocalTime.of(7, 40), LocalTime.of(8, 30), 50));
        var segundo = blocos.saveAndFlush(new BlocoHorario(LocalTime.of(12, 10), LocalTime.of(13, 0), 50));
        var pagina = PageRequest.of(0, 1);
        var todos = blocos.buscarPorFiltros(null, null, null, null, pagina);
        assertThat(todos.getTotalElements()).isEqualTo(2);
        assertThat(todos.getContent()).extracting(BlocoHorario::getId).containsExactly(primeiro.getId());
        assertThat(blocos.buscarPorFiltros(null, null, null, null, PageRequest.of(1, 1)).getContent())
                .extracting(BlocoHorario::getId).containsExactly(segundo.getId());
        assertThat(blocos.buscarPorFiltros(LocalTime.of(7, 40), null, null, null, pagina).getTotalElements()).isEqualTo(1);
        assertThat(blocos.buscarPorFiltros(null, LocalTime.of(13, 0), null, null, pagina).getTotalElements()).isEqualTo(1);
        assertThat(blocos.buscarPorFiltros(null, null, 50, null, pagina).getTotalElements()).isEqualTo(2);
        assertThat(blocos.buscarPorFiltros(null, null, null, "12:10", pagina).getTotalElements()).isEqualTo(1);
        assertThat(blocos.buscarPorFiltros(LocalTime.of(7, 40), LocalTime.of(8, 30), 50, "07:40", pagina).getTotalElements()).isEqualTo(1);
        assertThat(blocos.buscarPorFiltros(LocalTime.of(1, 0), null, null, null, pagina).getTotalElements()).isZero();
    }

    @BeforeEach
    void prepararDados() {
        var curso = new Curso("Curso de teste", "Anual", Status.ATIVO, 2);
        var periodo = new PeriodoAtividadeQuadro(2026, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30),
                Status.ATIVO);
        periodo.setTipoPeridoAtividadeQuadro(TipoPeridoAtividadeQuadro.SEMESTRAL);
        entityManager.persist(curso);
        entityManager.persist(periodo);
        var quadro = new QuadroHorario(1, Status.INATIVO);
        quadro.setCurso(curso);
        quadro.setPeriodoAtividadeQuadro(periodo);
        quadro.setDataCriacao(LocalDateTime.of(2026, 2, 10, 10, 0));
        entityManager.persist(quadro);
        entityManager.flush();
    }

    @DynamicPropertySource
    static void postgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> System.getenv("QUADRO_TEST_PG_URL"));
        registry.add("spring.datasource.username", () -> "gini_test");
        registry.add("spring.datasource.password", () -> "gini_test");
    }

    @Test
    void ordinalSemestralEAnualPermaneceCompativel() {
        var semestral = periodos.findAll().stream().filter(p -> p.getAno() == 2026).findFirst().orElseThrow();
        assertThat(semestral.getTipoPeridoAtividadeQuadro()).isEqualTo(TipoPeridoAtividadeQuadro.SEMESTRAL);
        var ordinal = ((Number) entityManager.createNativeQuery("SELECT tipo_periodo_atividade_quadro FROM periodo_atividade_quadro WHERE id_periodo_atividade_quadro = :id")
                .setParameter("id", semestral.getId()).getSingleResult()).intValue();
        assertThat(ordinal).isZero();
        var anual = new PeriodoAtividadeQuadro(2027, 1, LocalDate.of(2027, 1, 1), LocalDate.of(2027, 12, 31), Status.ATIVO);
        anual.setTipoPeridoAtividadeQuadro(TipoPeridoAtividadeQuadro.ANUAL);
        entityManager.persist(anual);
        entityManager.flush();
        var anualOrdinal = ((Number) entityManager.createNativeQuery("SELECT tipo_periodo_atividade_quadro FROM periodo_atividade_quadro WHERE id_periodo_atividade_quadro = :id")
                .setParameter("id", anual.getId()).getSingleResult()).intValue();
        assertThat(anualOrdinal).isEqualTo(1);
        entityManager.clear();
        assertThat(periodos.findById(anual.getId()).orElseThrow().getTipoPeridoAtividadeQuadro()).isEqualTo(TipoPeridoAtividadeQuadro.ANUAL);
    }

    @Test
    void listaQuadrosComDataAusenteEFiltraPorDia() {
        var pagina = PageRequest.of(0, 100);
        var todos = quadros.buscarComFiltros(null, null, null, null, null, null, null, null, pagina);
        assertThat(todos.getTotalElements()).isPositive();
        var quadro = todos.getContent().getFirst();
        var inicio = quadro.getDataCriacao().toLocalDate().atStartOfDay();
        var filtrados = quadros.buscarComFiltros(quadro.getCurso().getId(), null, null, null, null, inicio,
                inicio.plusDays(1), null, pagina);
        assertThat(filtrados.getContent()).extracting(q -> q.getId()).contains(quadro.getId());
        var ausentes = quadros.buscarComFiltros(null, null, null, null, null, LocalDateTime.of(1900, 1, 1, 0, 0),
                LocalDateTime.of(1900, 1, 2, 0, 0), null, pagina);
        assertThat(ausentes.getTotalElements()).isZero();
    }

    @Test
    void listaPeriodosComDatasAusentesEFiltraDatas() {
        var pagina = PageRequest.of(0, 100);
        var todos = periodos.buscarPorFiltros(null, null, null, null, null, pagina);
        assertThat(todos.getTotalElements()).isPositive();
        var periodo = todos.getContent().getFirst();
        var filtrados = periodos.buscarPorFiltros(null, null, null, periodo.getDataInicio(), periodo.getDataFim(),
                pagina);
        assertThat(filtrados.getContent()).extracting(p -> p.getId()).contains(periodo.getId());
    }
}
