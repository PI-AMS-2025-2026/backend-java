package com.fatec.gini.domain.services.usecase.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.domain.entities.ProfessorDisciplina;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.services.QuadroHorarioService;
import com.fatec.gini.domain.services.AlocacaoService;
import com.fatec.gini.dto.alocacao.AlocacaoRequest;
import com.fatec.gini.dto.id.LongDTO;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioRequest;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.SalaRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorRepository;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorDisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;
import com.fatec.gini.web.exception.AlocacaoLoteException;

@SpringBootTest(properties = "app.security.authorization.enabled=false")
@ActiveProfiles("test")
@Transactional
class AlocacaoFluxoIntegracaoTest {

    @Autowired private CriarAlocacaoUseCase criarAlocacao;
    @Autowired private AtualizarAlocacaoUseCase atualizarAlocacao;
    @Autowired private AlocacaoRepository alocacoes;
    @Autowired private TurmaRepository turmas;
    @Autowired private DisciplinaRepository disciplinas;
    @Autowired private SalaRepository salas;
    @Autowired private ProfessorRepository professores;
    @Autowired private DisponibilidadeProfessorRepository disponibilidades;
    @Autowired private ProfessorDisciplinaRepository professorDisciplinas;
    @Autowired private BlocoHorarioRepository blocos;
    @Autowired private QuadroHorarioRepository quadros;
    @Autowired private EntityManager entityManager;
    @Autowired private QuadroHorarioService quadroService;
    @Autowired private AlocacaoService alocacaoService;

    @BeforeEach
    void isolarCenarioDeAlocacoes() {
        alocacoes.deleteAll();
    }

    @Test
    void persisteTurmaAssociadaAoQuadro() {
        var turma = turmas.findAll().stream()
                .filter(item -> item.getCurso().getNome().equals("Analise e Desenvolvimento de Sistemas"))
                .findFirst()
                .orElseThrow();
        var quadro = quadros.findAll().stream()
                .filter(item -> item.getCurso().getId().equals(turma.getCurso().getId()))
                .filter(item -> item.getStatus() == Status.ATIVO)
                .findFirst()
                .orElseThrow();

        quadro.setTurma(turma);
        quadros.saveAndFlush(quadro);
        entityManager.clear();

        var carregado = quadros.findById(quadro.getId()).orElseThrow();
        assertThat(carregado.getTurma().getId()).isEqualTo(turma.getId());
    }

    @Test
    void criaVersoesIndependentesParaTurmasDoMesmoCurso() {
        var primeira = turmas.findAll().stream()
                .filter(t -> t.getCurso().getNome().equals("Analise e Desenvolvimento de Sistemas"))
                .findFirst().orElseThrow();
        var curso = primeira.getCurso();
        primeira = new Turma();
        primeira.setCurso(curso);
        primeira.setAno(9998);
        primeira.setPeriodo(1);
        primeira.setCodigo("teste-9998");
        primeira.setNumeroAlunos(20);
        primeira = turmas.saveAndFlush(primeira);
        var periodo = quadros.findAll().stream()
                .filter(q -> q.getCurso().getId().equals(curso.getId()))
                .filter(q -> q.getPeriodoAtividadeQuadro().getStatus() == Status.ATIVO)
                .findFirst().orElseThrow().getPeriodoAtividadeQuadro();
        var segunda = new Turma();
        segunda.setCurso(primeira.getCurso());
        segunda.setAno(9999);
        segunda.setPeriodo(1);
        segunda.setCodigo("teste-9999");
        segunda.setNumeroAlunos(20);
        segunda = turmas.saveAndFlush(segunda);

        var cursoId = new LongDTO(primeira.getCurso().getId());
        var periodoId = new LongDTO(periodo.getId());
        var a1 = quadroService.criar(new QuadroHorarioRequest(1, Status.ATIVO, cursoId, new LongDTO(primeira.getId()), periodoId));
        var b1 = quadroService.criar(new QuadroHorarioRequest(1, Status.ATIVO, cursoId, new LongDTO(segunda.getId()), periodoId));
        assertThat(a1.versao()).isEqualTo(1);
        assertThat(b1.versao()).isEqualTo(1);
        assertThat(a1.turma().id()).isEqualTo(primeira.getId());
        assertThat(b1.turma().id()).isEqualTo(segunda.getId());
        quadroService.inativar(a1.id());
        var a2 = quadroService.criar(new QuadroHorarioRequest(1, Status.ATIVO, cursoId, new LongDTO(primeira.getId()), periodoId));
        assertThat(a2.versao()).isEqualTo(2);
        assertThat(quadros.findTopByTurmaIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(segunda.getId(), periodo.getId()).orElseThrow().getVersao()).isEqualTo(1);
        var historico = quadroService.listar(cursoId.id(), primeira.getId(), periodoId.id(), null, null, null, null, 0, 20);
        assertThat(historico.content()).extracting(q -> q.id()).containsExactlyInAnyOrder(a1.id(), a2.id());
        assertThat(quadroService.buscarPorId(a1.id()).versao()).isEqualTo(1);
        var outroPeriodo = new PeriodoAtividadeQuadro();
        outroPeriodo.setAno(9999);
        outroPeriodo.setPeriodo(1);
        outroPeriodo.setDataInicio(java.time.LocalDate.of(9999, 1, 1));
        outroPeriodo.setDataFim(java.time.LocalDate.of(9999, 6, 30));
        outroPeriodo.setStatus(Status.ATIVO);
        outroPeriodo.setTipoPeridoAtividadeQuadro(periodo.getTipoPeridoAtividadeQuadro());
        entityManager.persist(outroPeriodo);
        entityManager.flush();
        var novaVigencia = quadroService.criar(new QuadroHorarioRequest(1, Status.ATIVO, cursoId,
                new LongDTO(primeira.getId()), new LongDTO(outroPeriodo.getId())));
        assertThat(novaVigencia.versao()).isEqualTo(1);
        assertThat(quadroService.listar(cursoId.id(), primeira.getId(), periodoId.id(), null, null,
                null, null, 0, 20).content()).hasSize(2);
    }

    @Test
    void criaAlocacaoComTipoDeSalaCompativelNoFluxoCompleto() {
        var sala = salas.findById(5L).orElseThrow();
        var disciplina = disciplinas.findById(4L).orElseThrow();
        assertThat(sala.getTipoSala().getId()).isEqualTo(disciplina.getTipoSala().getId());

        Alocacao alocacao = alocacao(sala.getId());
        Alocacao salva = criarAlocacao.executar(alocacao, 1L);

        assertThat(salva.getId()).isNotNull();
        assertThat(alocacoes.findById(salva.getId())).isPresent();
    }

    @Test
    void rejeitaSalaDeTipoDiferente() {
        assertThatThrownBy(() -> criarAlocacao.executar(alocacao(1L), 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("incompatível com o tipo de sala");
    }

    @Test
    void identificaSegundaAulaDoLoteQuandoSalaForIncompativel() {
        long quadroId = alocacao(5L).getQuadroHorario().getId();
        var valida = new AlocacaoRequest(1L, 4L, 5L, 5L, DiaSemana.SEGUNDA, 1L, quadroId, 1L);
        var invalida = new AlocacaoRequest(1L, 4L, 1L, 5L, DiaSemana.QUARTA, 1L, quadroId, 1L);

        assertThatThrownBy(() -> alocacaoService.criarLote(List.of(valida, invalida)))
                .isInstanceOfSatisfying(AlocacaoLoteException.class, erro -> {
                    assertThat(erro.getIndice()).isEqualTo(1);
                    assertThat(erro.getCause()).isInstanceOf(BusinessException.class)
                            .hasMessageContaining("incompatível com o tipo de sala");
                });
    }

    @Test
    void permiteEditarSemConfundirAPropriaAlocacaoComConflito() {
        Long id = criarAlocacao.executar(alocacao(5L), 1L).getId();

        Alocacao atualizada = atualizarAlocacao.executar(id, alocacao(5L), 1L);

        assertThat(atualizada.getId()).isEqualTo(id);
    }

    @Test
    void rejeitaTrocaParaSalaOcupadaMesmoComTipoCompativel() {
        professorDisciplinas.save(new ProfessorDisciplina(
                professores.findById(4L).orElseThrow(),
                disciplinas.findById(4L).orElseThrow()));
        disponibilidades.save(new DisponibilidadeProfessor(
                professores.findById(4L).orElseThrow(),
                DiaSemana.SEGUNDA,
                blocos.findById(1L).orElseThrow()));
        criarAlocacao.executar(
                alocacao(5L, 4L, 2L, 4L, DiaSemana.SEGUNDA, 1L, 2L),
                1L);
        Long id = criarAlocacao.executar(alocacao(5L), 1L).getId();

        assertThatThrownBy(() -> atualizarAlocacao.executar(id, alocacao(2L), 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já está ocupada");
    }

    @Test
    void criaTresExemplosAdicionaisSemConflitoEntreSi() {
        criarAlocacao.executar(alocacao(5L), 1L);

        var exemplos = new Alocacao[] {
                alocacao(2L, 2L, 1L, 1L, DiaSemana.TERÇA, 7L, 1L),
                alocacao(2L, 15L, 2L, 2L, DiaSemana.SEGUNDA, 7L, 1L),
                alocacao(1L, 4L, 5L, 5L, DiaSemana.TERÇA, 1L, 2L)
        };

        for (Alocacao exemplo : exemplos) {
            var salva = criarAlocacao.executar(exemplo, 1L);
            assertThat(salva.getId()).isNotNull();
            assertThat(alocacoes.findById(salva.getId())).isPresent();
        }
    }

    @Test
    void persisteProfessorSelecionadoEntreVinculados() {
        var professor = professores.findById(4L).orElseThrow();
        professorDisciplinas.save(new ProfessorDisciplina(professor, disciplinas.findById(4L).orElseThrow()));
        disponibilidades.save(new DisponibilidadeProfessor(professor, DiaSemana.SEGUNDA, blocos.findById(1L).orElseThrow()));
        var salva = criarAlocacao.executar(alocacao(1L, 4L, 5L, 4L, DiaSemana.SEGUNDA, 1L, 2L), 1L);
        entityManager.flush();
        entityManager.clear();
        assertThat(alocacoes.findById(salva.getId()).orElseThrow().getProfessor().getId()).isEqualTo(4L);
    }

    private Alocacao alocacao(Long salaId) {
        return alocacao(1L, 4L, salaId, 5L, DiaSemana.SEGUNDA, 1L, 2L);
    }

    private Alocacao alocacao(Long turmaId, Long disciplinaId, Long salaId, Long professorId,
            DiaSemana dia, Long blocoId, Long quadroId) {
        var turma = turmas.findById(turmaId).orElseThrow();
        var original = quadros.findById(quadroId).orElseThrow();
        var quadro = quadros.findAll().stream()
                .filter(q -> q.getStatus() == Status.ATIVO && q.getTurma() != null)
                .filter(q -> q.getTurma().getId().equals(turmaId))
                .filter(q -> q.getPeriodoAtividadeQuadro().getId().equals(original.getPeriodoAtividadeQuadro().getId()))
                .findFirst().orElseGet(() -> {
                    var criado = new QuadroHorario(1, Status.ATIVO);
                    criado.setTurma(turma);
                    criado.setCurso(turma.getCurso());
                    criado.setPeriodoAtividadeQuadro(original.getPeriodoAtividadeQuadro());
                    criado.setDataCriacao(java.time.LocalDateTime.now());
                    return quadros.saveAndFlush(criado);
                });
        return new Alocacao(
                turma,
                disciplinas.findById(disciplinaId).orElseThrow(),
                salas.findById(salaId).orElseThrow(),
                professores.findById(professorId).orElseThrow(),
                dia,
                blocos.findById(blocoId).orElseThrow(),
                quadro);
    }
}
