package com.fatec.gini.domain.services.usecase.write;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.services.usecase.read.ValidarConflitoSalaBlocoHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarConflitoTurmaBlocoHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDisponibilidadeProfessorUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDuplicidadeAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarQuadroHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarReferenciasObrigatoriasAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarVinculoProfessorDisciplinaUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class CriarAlocacaoUseCaseTest {

    @InjectMocks
    private CriarAlocacaoUseCase useCase;

    @Mock
    private ValidarReferenciasObrigatoriasAlocacaoUseCase validarRefObrigatorias;

    @Mock
    private ValidarQuadroHorarioUseCase validarQuadroHorario;

    @Mock
    private ValidarVinculoProfessorDisciplinaUseCase validarVincProfDisciplina;

    @Mock
    private ValidarDisponibilidadeProfessorUseCase validarDisponibilidadeProfessor;

    @Mock
    private ValidarConflitoTurmaBlocoHorarioUseCase validarConflitoTurmaBlocoHorario;

    @Mock
    private ValidarConflitoSalaBlocoHorarioUseCase validarConflitoSalaBlocoHorario;

    @Mock
    private ValidarDuplicidadeAlocacaoUseCase validarDuplicidade;

    @Mock
    private RegistrarHistoricoAlocacaoUseCase historicoAlocacaoUseCase;

    @Mock
    private AlocacaoRepository alocacaoRepository;

    @Test
    void deveCriarAlocacaoComSucesso() {
        Alocacao entity = criarAlocacaoBase();
        Usuario usuarioAlteracao = new Usuario();
        usuarioAlteracao.setId(99L);

        Alocacao alocacaoSalva = criarAlocacaoBase();
        alocacaoSalva.setId(10L);

        when(validarRefObrigatorias.validar(entity)).thenReturn(entity);
        when(validarRefObrigatorias.buscarUsuarioAlteracaoPorId(99L)).thenReturn(usuarioAlteracao);
        doNothing().when(validarQuadroHorario).executar(entity);
        doNothing().when(validarVincProfDisciplina).executar(entity);
        doNothing().when(validarDisponibilidadeProfessor).executar(entity);
        doNothing().when(validarConflitoTurmaBlocoHorario).executar(entity);
        doNothing().when(validarConflitoSalaBlocoHorario).executar(entity);
        doNothing().when(validarDuplicidade).executarCriacao(entity);
        when(alocacaoRepository.save(entity)).thenReturn(alocacaoSalva);

        Alocacao resultado = useCase.executar(entity, 99L);

        assertSame(alocacaoSalva, resultado);
        verify(historicoAlocacaoUseCase).registrarCriacao(alocacaoSalva, usuarioAlteracao);
    }

    @Test
    void deveLancarExcecaoQuandoDuplicidadeNaCriacao() {
        Alocacao entity = criarAlocacaoBase();
        Usuario usuarioAlteracao = new Usuario();
        usuarioAlteracao.setId(99L);

        when(validarRefObrigatorias.validar(entity)).thenReturn(entity);
        when(validarRefObrigatorias.buscarUsuarioAlteracaoPorId(99L)).thenReturn(usuarioAlteracao);
        doNothing().when(validarQuadroHorario).executar(entity);
        doNothing().when(validarVincProfDisciplina).executar(entity);
        doNothing().when(validarDisponibilidadeProfessor).executar(entity);
        doNothing().when(validarConflitoTurmaBlocoHorario).executar(entity);
        doNothing().when(validarConflitoSalaBlocoHorario).executar(entity);
        doThrow(new BusinessException("duplicidade")).when(validarDuplicidade).executarCriacao(entity);

        assertThrows(BusinessException.class, () -> useCase.executar(entity, 99L));

        verify(alocacaoRepository, never()).save(entity);
        verify(historicoAlocacaoUseCase, never()).registrarCriacao(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    private Alocacao criarAlocacaoBase() {
        Turma turma = new Turma();
        turma.setId(1L);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(2L);

        Sala sala = new Sala();
        sala.setId(3L);

        Professor professor = new Professor();
        professor.setId(4L);

        DiaSemana diaSemana = new DiaSemana();
        diaSemana.setId(5L);

        BlocoHorario blocoHorario = new BlocoHorario();
        blocoHorario.setId(6L);

        QuadroHorario quadroHorario = new QuadroHorario();
        quadroHorario.setCurso(new com.fatec.gini.domain.entities.Curso());
        quadroHorario.getCurso().setId(7L);
        quadroHorario.setPeriodoLetivo(new com.fatec.gini.domain.entities.PeriodoLetivo());
        quadroHorario.getPeriodoLetivo().setId(8L);

        Alocacao alocacao = new Alocacao();
        alocacao.setTurma(turma);
        alocacao.setDisciplina(disciplina);
        alocacao.setSala(sala);
        alocacao.setProfessor(professor);
        alocacao.setDiaSemana(diaSemana);
        alocacao.setBlocoHorario(blocoHorario);
        alocacao.setQuadroHorario(quadroHorario);

        return alocacao;
    }
}
