package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDuplicidadeAlocacaoLoteUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarSugestaoAutomatica;
import com.fatec.gini.domain.services.usecase.write.AtualizarAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.write.CriarAlocacaoUseCase;
import com.fatec.gini.dto.id.LongDTO;
import com.fatec.gini.dto.sugestaoAutomatica.SugestaoRequest;
import com.fatec.gini.dto.sugestaoAutomatica.SugestaoResponse;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;

@ExtendWith(MockitoExtension.class)
class AlocacaoServiceTest {

    @Mock
    private AlocacaoRepository repository;

    @Mock
    private CriarAlocacaoUseCase criarAlocacaoUseCase;

    @Mock
    private AtualizarAlocacaoUseCase atualizarAlocacaoUseCase;

    @Mock
    private ValidarDuplicidadeAlocacaoLoteUseCase alocacaoLoteUseCase;

    @Mock
    private ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;

    @Mock
    private ValidarSugestaoAutomatica validarSugestaoAutomatica;

    @InjectMocks
    private AlocacaoService service;

    @Test
    void deveRetornarSucessoQuandoTentativaForValida() {
        SugestaoRequest request = request(1L, 2L, 3L, 4L, 6L, 7L);
        when(validarSugestaoAutomatica.identificarMotivo(any(Alocacao.class)))
                .thenReturn(null);

        SugestaoResponse response = service.sugerirAlternativas(request);

        assertThat(response.sucesso()).isTrue();
        assertThat(response.mensagem()).isEqualTo("A combinação informada é válida.");
        assertThat(response.motivo()).isNull();
        assertThat(response.sugestoes()).isEmpty();
        verify(validarSugestaoAutomatica, never()).executar(any(Alocacao.class));
    }

    @Test
    void deveRetornarMotivoEAlternativasQuandoTentativaForInvalida() {
        SugestaoRequest request = request(1L, 2L, 3L, 4L, 6L, 7L);
        Alocacao alternativa = alocacao(11L, 12L, 13L, 14L, 16L, 17L, DiaSemana.QUARTA);

        when(validarSugestaoAutomatica.identificarMotivo(any(Alocacao.class)))
                .thenReturn("Professor indisponível");
        when(validarSugestaoAutomatica.executar(any(Alocacao.class)))
                .thenReturn(List.of(alternativa));

        SugestaoResponse response = service.sugerirAlternativas(request);

        assertThat(response.sucesso()).isFalse();
        assertThat(response.mensagem()).isEqualTo("Não é possível realizar a alocação.");
        assertThat(response.motivo()).isEqualTo("Professor indisponível");
        assertThat(response.sugestoes()).hasSize(1);
        assertThat(response.sugestoes().get(0))
                .usingRecursiveComparison()
                .isEqualTo(new com.fatec.gini.dto.alocacao.AlocacaoRequest(
                        11L, 12L, 13L, 14L, DiaSemana.QUARTA, 16L, 17L, null, null));
    }

    private SugestaoRequest request(
            long turmaId,
            long disciplinaId,
            long salaId,
            long professorId,
            long horarioId,
            long quadroHorarioId) {
        return new SugestaoRequest(
                new LongDTO(turmaId),
                new LongDTO(disciplinaId),
                new LongDTO(salaId),
                new LongDTO(professorId),
                DiaSemana.SEGUNDA,
                new LongDTO(horarioId),
                new LongDTO(quadroHorarioId));
    }

    private Alocacao alocacao(
            long turmaId,
            long disciplinaId,
            long salaId,
            long professorId,
            long horarioId,
            long quadroHorarioId,
            DiaSemana diaSemana) {
        Turma turma = new Turma();
        turma.setId(turmaId);
        Disciplina disciplina = new Disciplina();
        disciplina.setId(disciplinaId);
        Sala sala = new Sala();
        sala.setId(salaId);
        Professor professor = new Professor();
        professor.setId(professorId);
        BlocoHorario horario = new BlocoHorario();
        horario.setId(horarioId);
        QuadroHorario quadroHorario = new QuadroHorario();
        quadroHorario.setId(quadroHorarioId);

        return new Alocacao(turma, disciplina, sala, professor, diaSemana, horario, quadroHorario);
    }
}