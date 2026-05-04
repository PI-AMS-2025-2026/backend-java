package com.fatec.horario.domain.services.usecase.write;

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

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.domain.entities.Turma;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.domain.services.usecase.read.ValidarConflitoTurmaHorarioUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarDisponibilidadeProfessorUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarDuplicidadeAlocacaoUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarGradeHorariaUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarReferenciasObrigatoriasAlocacaoUseCase;
import com.fatec.horario.domain.services.usecase.read.ValidarVinculoProfessorDisciplinaUseCase;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class AtualizarAlocacaoUseCaseTest {

    @InjectMocks
    private AtualizarAlocacaoUseCase useCase;

    @Mock
    private ValidarReferenciasObrigatoriasAlocacaoUseCase validarRefObrigatorias;

    @Mock
    private ValidarGradeHorariaUseCase validarGradeHoraria;

    @Mock
    private ValidarVinculoProfessorDisciplinaUseCase validarVincProfDisciplina;

    @Mock
    private ValidarDisponibilidadeProfessorUseCase validarDisponibilidadeProfessor;

    @Mock
    private ValidarConflitoTurmaHorarioUseCase validarConflitoTurmaHorario;

    @Mock
    private ValidarDuplicidadeAlocacaoUseCase validarDuplicidade;

    @Mock
    private RegistrarHistoricoAlocacaoUseCase historicoAlocacaoUseCase;

    @Mock
    private AlocacaoRepository alocacaoRepository;

    @Test
    void deveAtualizarAlocacaoComSucesso() {
        Alocacao entity = criarAlocacaoBase();
        entity.setId(10L);

        Usuario usuarioAlteracao = new Usuario();
        usuarioAlteracao.setId(99L);

        Alocacao alocacaoSalva = criarAlocacaoBase();
        alocacaoSalva.setId(10L);

        when(validarRefObrigatorias.validar(entity)).thenReturn(entity);
        when(validarRefObrigatorias.buscarUsuarioAlteracaoPorId(99L)).thenReturn(usuarioAlteracao);
        doNothing().when(validarGradeHoraria).executar(entity);
        doNothing().when(validarVincProfDisciplina).executar(entity);
        doNothing().when(validarDisponibilidadeProfessor).executar(entity);
        doNothing().when(validarConflitoTurmaHorario).executar(entity);
        doNothing().when(validarDuplicidade).executarAtualizacao(entity);
        when(alocacaoRepository.save(entity)).thenReturn(alocacaoSalva);

        Alocacao resultado = useCase.executar(10L, entity, 99L, "Ajuste necessario");

        assertSame(alocacaoSalva, resultado);
        verify(historicoAlocacaoUseCase).registrarAtualizacao(alocacaoSalva, entity, usuarioAlteracao, "Ajuste necessario");
    }

    @Test
    void deveLancarExcecaoQuandoSemDisponibilidadeProfessor() {
        Alocacao entity = criarAlocacaoBase();
        entity.setId(10L);

        Usuario usuarioAlteracao = new Usuario();
        usuarioAlteracao.setId(99L);

        when(validarRefObrigatorias.validar(entity)).thenReturn(entity);
        when(validarRefObrigatorias.buscarUsuarioAlteracaoPorId(99L)).thenReturn(usuarioAlteracao);
        doNothing().when(validarGradeHoraria).executar(entity);
        doNothing().when(validarVincProfDisciplina).executar(entity);
        doThrow(new BusinessException("sem disponibilidade")).when(validarDisponibilidadeProfessor).executar(entity);

        assertThrows(BusinessException.class,
                () -> useCase.executar(10L, entity, 99L, "Ajuste necessario"));

        verify(alocacaoRepository, never()).save(entity);
        verify(historicoAlocacaoUseCase, never()).registrarAtualizacao(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString());
    }

    private Alocacao criarAlocacaoBase() {
        Turma turma = new Turma();
        turma.setId(1L);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(2L);

        Sala sala = new Sala();
        sala.setId(3L);

        Usuario usuario = new Usuario();
        usuario.setId(4L);

        DiaSemana diaSemana = new DiaSemana();
        diaSemana.setId(5L);

        Horario horario = new Horario();
        horario.setId(6L);

        GradeHoraria gradeHoraria = new GradeHoraria();
        gradeHoraria.setCurso(new com.fatec.horario.domain.entities.Curso());
        gradeHoraria.getCurso().setId(7L);
        gradeHoraria.setPeriodoLetivo(new com.fatec.horario.domain.entities.PeriodoLetivo());
        gradeHoraria.getPeriodoLetivo().setId(8L);

        Alocacao alocacao = new Alocacao();
        alocacao.setTurma(turma);
        alocacao.setDisciplina(disciplina);
        alocacao.setSala(sala);
        alocacao.setUsuario(usuario);
        alocacao.setDiaSemana(diaSemana);
        alocacao.setHorario(horario);
        alocacao.setGradeHoraria(gradeHoraria);

        return alocacao;
    }
}
