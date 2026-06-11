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
import com.fatec.gini.domain.entities.GradeHoraria;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.services.usecase.read.ValidarConflitoSalaBlocoHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarConflitoTurmaBlocoHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDisponibilidadeProfessorUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDuplicidadeAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarGradeHorariaUseCase;
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
    private ValidarGradeHorariaUseCase validarGradeHoraria;

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
        doNothing().when(validarGradeHoraria).executar(entity);
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
        doNothing().when(validarGradeHoraria).executar(entity);
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

        GradeHoraria gradeHoraria = new GradeHoraria();
        gradeHoraria.setCurso(new com.fatec.gini.domain.entities.Curso());
        gradeHoraria.getCurso().setId(7L);
        gradeHoraria.setPeriodoLetivo(new com.fatec.gini.domain.entities.PeriodoLetivo());
        gradeHoraria.getPeriodoLetivo().setId(8L);

        Alocacao alocacao = new Alocacao();
        alocacao.setTurma(turma);
        alocacao.setDisciplina(disciplina);
        alocacao.setSala(sala);
        alocacao.setProfessor(professor);
        alocacao.setDiaSemana(diaSemana);
        alocacao.setBlocoHorario(blocoHorario);
        alocacao.setGradeHoraria(gradeHoraria);

        return alocacao;
    }
}
