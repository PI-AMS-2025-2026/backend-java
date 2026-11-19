package com.fatec.gini.infrastructure.seed;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "app.security.authorization.enabled=false")
@ActiveProfiles("test")
class DadosIniciaisCoerenciaTest {
    @Autowired JdbcTemplate jdbc;

    @Test
    void quadrosAtivosEAlocacoesRespeitamTurmaCursoEEtapa() {
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM usuario WHERE tipo_usuario = 'COORDENADOR' AND status = 'ATIVO' AND id_curso IS NULL", Integer.class)).as("coordenadores sem curso").isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM quadro_horario WHERE status = 'ATIVO' AND id_turma IS NULL", Integer.class)).as("quadros ativos sem turma").isZero();
        assertThat(jdbc.queryForList("SELECT a.id_alocacao, t.codigo, t.periodo AS turma_periodo, d.cod_disciplina, d.periodo AS disciplina_periodo, q.id_turma AS quadro_turma, t.id_turma FROM alocacao a JOIN quadro_horario q ON q.id_quadro_horario = a.id_quadro_horario JOIN turma t ON t.id_turma = a.id_turma JOIN disciplina d ON d.id_disciplina = a.id_disciplina WHERE q.id_turma IS NULL OR q.id_turma <> t.id_turma OR q.id_curso <> t.id_curso OR d.id_curso <> t.id_curso OR d.periodo <> t.periodo")).as("alocações inconsistentes").isEmpty();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM (SELECT id_turma, id_periodo_atividade_quadro FROM quadro_horario WHERE status = 'ATIVO' GROUP BY id_turma, id_periodo_atividade_quadro HAVING COUNT(*) > 1) x", Integer.class)).as("quadros ativos duplicados").isZero();
    }
}
