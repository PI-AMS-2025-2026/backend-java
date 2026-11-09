package com.fatec.gini.infrastructure.migrations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/** Tests the packaged migration against isolated databases without Spring SQL initialization. */
class ImportacaoPlanilhaFatecMigrationTest {

    private static final String SOURCE_HASH =
            "b0e54449693c8628a640c25b495c099da28d9da8391b8f780fad3db76dd2ce42";

    @Test
    void importaTodasAsAbasSemDataSqlENaoInventaHorariosIncompletos() throws Exception {
        try (var database = h2Database()) {
            assertThat(database.flyway().migrate().migrationsExecuted).isEqualTo(1);
            assertImport(database.connection());
            assertThat(number(database.connection(), "select count(*) from curso")).isEqualTo(9);
            assertThat(number(database.connection(), "select count(*) from professor")).isEqualTo(138);
            assertThat(number(database.connection(), "select count(*) from disciplina")).isEqualTo(672);
        }
    }

    @Test
    void preservaRegistrosExistentesEResolveColisaoDeCodigoSemIdsFixos() throws Exception {
        try (var database = h2Database()) {
            var connection = database.connection();
            execute(connection, """
                    insert into curso (nome, periodicidade, duracao, status)
                    values ('Análise e Desenvolvimento de Sistemas', 'Semestral', 6, 'INATIVO')
                    """);
            execute(connection, "insert into tipo_sala (nome) values ('Sala de Aula')");
            execute(connection, """
                    insert into disciplina (nome, carga_horaria, tipo_disciplina, periodo,
                        modalidade, cod_disciplina, id_curso, id_tipo_sala)
                    select 'Registro existente', 40, 'Teórica', 1, 'Presencial', 'TGE-103',
                        c.id_curso, t.id_tipo_sala from curso c cross join tipo_sala t
                    """);
            execute(connection, """
                    insert into professor (nome, email, status)
                    values ('Registro existente', 'registro.existente@example.invalid', 'INATIVO')
                    """);
            assertThat(database.flyway().migrate().migrationsExecuted).isEqualTo(1);
            assertImport(connection);
            assertThat(text(connection, "select status from curso where nome = 'Análise e Desenvolvimento de Sistemas'"))
                    .isEqualTo("INATIVO");
            assertThat(text(connection, "select nome from disciplina where cod_disciplina = 'TGE-103'"))
                    .isEqualTo("Registro existente");
            assertThat(text(connection, "select status from professor where email = 'registro.existente@example.invalid'"))
                    .isEqualTo("INATIVO");
            assertThat(number(connection, """
                    select count(*) from importacao_planilha_fatec i
                    join disciplina d on d.id_disciplina = i.id_disciplina
                    where i.aba = 'Matrizes' and d.nome = 'Projeto Integrador em Gestão Empresarial I'
                    and d.cod_disciplina like 'PL26-M%'
                    """)).isEqualTo(1);
        }
    }

    @Test
    void flywayNaoReaplicaImportacaoJaExecutada() throws Exception {
        try (var database = h2Database()) {
            database.flyway().migrate();
            long allocations = number(database.connection(), "select count(*) from alocacao");
            long sources = number(database.connection(), "select count(*) from importacao_planilha_fatec");
            assertThat(database.flyway().migrate().migrationsExecuted).isZero();
            assertThat(number(database.connection(), "select count(*) from alocacao")).isEqualTo(allocations);
            assertThat(number(database.connection(), "select count(*) from importacao_planilha_fatec")).isEqualTo(sources);
            database.flyway().validate();
        }
    }

    @Test
    void executaHistoricoFlywayCompletoEmPostgresqlQuandoBancoIsoladoDisponivel() throws Exception {
        String url = System.getenv("FATEC_TEST_POSTGRES_URL");
        assumeTrue(url != null && !url.isBlank(), "Banco PostgreSQL isolado não informado.");
        String schema = "fatec_migration_test_" + UUID.randomUUID().toString().replace("-", "");
        var flyway = Flyway.configure().dataSource(url, "postgres", "")
                .schemas(schema).defaultSchema(schema).locations("classpath:db/migration").load();
        try (var connection = DriverManager.getConnection(url, "postgres", "")) {
            assertThat(flyway.migrate().migrationsExecuted).isEqualTo(4);
            connection.setSchema(schema);
            assertImport(connection);
            flyway.validate();
            assertThat(flyway.migrate().migrationsExecuted).isZero();
        } finally {
            // The schema name is generated by this test; no existing application schema is removed.
            try (var connection = DriverManager.getConnection(url, "postgres", "")) {
                execute(connection, "drop schema if exists " + schema + " cascade");
            }
        }
    }

    private static Database h2Database() throws SQLException {
        String url = "jdbc:h2:mem:fatec_import_" + UUID.randomUUID()
                + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
        Connection connection = DriverManager.getConnection(url, "sa", "");
        ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("db/migration/V1_0_0__criacao_tabelas_iniciais.sql"));
        // V1_0_2 is a pre-existing PostgreSQL DO block. The initial schema already has its
        // textual day-of-week representation; baseline it only for the portable H2 test.
        var flyway = Flyway.configure().dataSource(url, "sa", "")
                .locations("classpath:db/migration")
                .baselineOnMigrate(true).baselineVersion("1.0.2").load();
        return new Database(connection, flyway);
    }

    private static void assertImport(Connection connection) throws SQLException {
        assertThat(number(connection, "select count(*) from importacao_planilha_fatec")).isEqualTo(852);
        assertThat(number(connection, "select count(*) from importacao_planilha_fatec where arquivo_sha256 = '"
                + SOURCE_HASH + "'")).isEqualTo(852);
        assertThat(number(connection, "select count(*) from importacao_planilha_fatec where aba = 'Cursos'"))
                .isEqualTo(9);
        assertThat(number(connection, "select count(*) from importacao_planilha_fatec where aba = 'Matrizes'"))
                .isEqualTo(280);
        assertThat(number(connection, "select count(*) from importacao_planilha_fatec where aba = 'Aulas'"))
                .isEqualTo(392);
        assertThat(number(connection, "select count(*) from importacao_planilha_fatec where aba = 'Professores'"))
                .isEqualTo(131);
        assertThat(number(connection, "select count(*) from importacao_planilha_fatec where aba = 'Coordenação'"))
                .isEqualTo(40);
        assertThat(number(connection, "select count(*) from importacao_planilha_fatec where aba = 'Aulas' and id_alocacao is null"))
                .isEqualTo(19);
        assertThat(number(connection, "select count(*) from importacao_planilha_fatec where aba = 'Aulas' and id_alocacao is not null"))
                .isEqualTo(373);
        assertThat(number(connection, """
                select count(*) from importacao_planilha_fatec i
                join alocacao a on a.id_alocacao = i.id_alocacao
                join disciplina d on d.id_disciplina = a.id_disciplina
                join turma t on t.id_turma = a.id_turma
                join quadro_horario q on q.id_quadro_horario = a.id_quadro_horario
                where d.id_curso <> t.id_curso or d.id_curso <> q.id_curso
                """)).isZero();
        assertThat(number(connection, """
                select count(*) from importacao_planilha_fatec i
                join quadro_horario q on q.id_quadro_horario = i.id_quadro_horario
                where q.status <> 'INATIVO'
                """)).isZero();
        assertThat(number(connection, """
                select count(*) from importacao_planilha_fatec i
                join usuario u on u.id_usuario = i.id_usuario
                where i.aba = 'Cursos' and u.status = 'INATIVO' and u.tipo_usuario = 'COORDENADOR'
                """)).isEqualTo(9);
        assertThat(number(connection, """
                select count(*) from importacao_planilha_fatec i join disciplina d on d.id_disciplina = i.id_disciplina
                join curso c on c.id_curso = d.id_curso
                where i.aba = 'Matrizes' and c.nome = 'Gestão da Tecnologia da Informação'
                and d.nome = 'Algoritmos' and d.carga_horaria = 67
                """)).isEqualTo(1);
        assertThat(number(connection, """
                select count(*) from importacao_planilha_fatec i join disciplina d on d.id_disciplina = i.id_disciplina
                where i.aba = 'Matrizes' and d.nome = 'Estágio Supervisionado em Gestão Empresarial'
                and d.carga_horaria = 240
                """)).isEqualTo(1);
        assertThat(number(connection, """
                select count(*) from importacao_planilha_fatec i join alocacao a on a.id_alocacao = i.id_alocacao
                join professor p on p.id_professor = a.id_professor
                join bloco_horario b on b.id_bloco_horario = a.id_bloco_horario
                where i.aba = 'Aulas' and i.linha = 376 and p.nome = 'Luis Bassani'
                and a.dia_semana = 'TERÇA' and b.hora_inicio = TIME '09:30:00' and b.hora_fim = TIME '11:10:00'
                """)).isEqualTo(1);
        assertThat(text(connection, "select dados_originais from importacao_planilha_fatec where aba = 'Coordenação' and linha = 11"))
                .contains("Juliana Lima");
        assertThat(number(connection, """
                select count(*) from importacao_planilha_fatec i join disciplina d on d.id_disciplina = i.id_disciplina
                where length(d.cod_disciplina) > 20 or d.cod_disciplina in ('SIGLA', 'XXX-XXX', 'Não publicado')
                """)).isZero();
        assertThat(number(connection, "select count(*) from alocacao where dia_semana in ('TERCA', 'SABADO')"))
                .isZero();
    }

    private static void execute(Connection connection, String sql) throws SQLException {
        try (var statement = connection.createStatement()) { statement.execute(sql); }
    }

    private static long number(Connection connection, String sql) throws SQLException {
        try (var statement = connection.createStatement(); ResultSet rows = statement.executeQuery(sql)) {
            assertThat(rows.next()).isTrue();
            return rows.getLong(1);
        }
    }

    private static String text(Connection connection, String sql) throws SQLException {
        try (var statement = connection.createStatement(); ResultSet rows = statement.executeQuery(sql)) {
            assertThat(rows.next()).isTrue();
            return rows.getString(1);
        }
    }

    private record Database(Connection connection, Flyway flyway) implements AutoCloseable {
        @Override public void close() throws SQLException { connection.close(); }
    }
}
