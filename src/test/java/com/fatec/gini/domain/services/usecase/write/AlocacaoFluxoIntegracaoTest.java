package com.fatec.gini.domain.services.usecase.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.SalaRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorRepository;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

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
    @Autowired private BlocoHorarioRepository blocos;
    @Autowired private QuadroHorarioRepository quadros;

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
    void permiteEditarSemConfundirAPropriaAlocacaoComConflito() {
        Long id = criarAlocacao.executar(alocacao(5L), 1L).getId();

        Alocacao atualizada = atualizarAlocacao.executar(id, alocacao(5L), 1L, "Ajuste de cadastro");

        assertThat(atualizada.getId()).isEqualTo(id);
    }

    @Test
    void rejeitaTrocaParaSalaOcupadaMesmoComTipoCompativel() {
        Long id = criarAlocacao.executar(alocacao(5L), 1L).getId();

        assertThatThrownBy(() -> atualizarAlocacao.executar(id, alocacao(2L), 1L, "Troca de sala"))
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

    private Alocacao alocacao(Long salaId) {
        return alocacao(1L, 4L, salaId, 5L, DiaSemana.SEGUNDA, 1L, 2L);
    }

    private Alocacao alocacao(Long turmaId, Long disciplinaId, Long salaId, Long professorId,
            DiaSemana dia, Long blocoId, Long quadroId) {
        return new Alocacao(
                turmas.findById(turmaId).orElseThrow(),
                disciplinas.findById(disciplinaId).orElseThrow(),
                salas.findById(salaId).orElseThrow(),
                professores.findById(professorId).orElseThrow(),
                dia,
                blocos.findById(blocoId).orElseThrow(),
                quadros.findById(quadroId).orElseThrow());
    }
}
