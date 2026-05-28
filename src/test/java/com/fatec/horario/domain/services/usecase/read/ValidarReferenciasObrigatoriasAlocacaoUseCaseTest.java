package com.fatec.horario.domain.services.usecase.read;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.GradeHoraria;
import com.fatec.gini.domain.entities.Horario;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.services.usecase.read.ValidarReferenciasObrigatoriasAlocacaoUseCase;
import com.fatec.gini.infrastructure.repositories.DiaSemanaRepository;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.gini.infrastructure.repositories.HorarioRepository;
import com.fatec.gini.infrastructure.repositories.SalaRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;
import com.fatec.gini.web.exception.ParameterException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ValidarReferenciasObrigatoriasAlocacaoUseCaseTest {

    @InjectMocks
    private ValidarReferenciasObrigatoriasAlocacaoUseCase useCase;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DiaSemanaRepository diaSemanaRepository;

    @Mock
    private HorarioRepository horarioRepository;

    @Mock
    private GradeHorariaRepository gradeHorariaRepository;

    @Test
    void deveValidarReferenciasObrigatoriasComSucesso() {
        Turma turmaEntrada = new Turma();
        turmaEntrada.setId(1L);
        Disciplina disciplinaEntrada = new Disciplina();
        disciplinaEntrada.setId(2L);
        Sala salaEntrada = new Sala();
        salaEntrada.setId(3L);
        Usuario usuarioEntrada = new Usuario();
        usuarioEntrada.setId(4L);
        DiaSemana diaSemanaEntrada = new DiaSemana();
        diaSemanaEntrada.setId(5L);
        Horario horarioEntrada = new Horario();
        horarioEntrada.setId(6L);
        GradeHoraria gradeEntrada = new GradeHoraria();
        setIdPrivadoGradeHoraria(gradeEntrada, 7L);

        Alocacao entity = new Alocacao();
        entity.setTurma(turmaEntrada);
        entity.setDisciplina(disciplinaEntrada);
        entity.setSala(salaEntrada);
        entity.setUsuario(usuarioEntrada);
        entity.setDiaSemana(diaSemanaEntrada);
        entity.setHorario(horarioEntrada);
        entity.setGradeHoraria(gradeEntrada);

        Turma turmaBanco = new Turma();
        turmaBanco.setId(1L);
        Disciplina disciplinaBanco = new Disciplina();
        disciplinaBanco.setId(2L);
        Sala salaBanco = new Sala();
        salaBanco.setId(3L);
        Usuario usuarioBanco = new Usuario();
        usuarioBanco.setId(4L);
        DiaSemana diaSemanaBanco = new DiaSemana();
        diaSemanaBanco.setId(5L);
        Horario horarioBanco = new Horario();
        horarioBanco.setId(6L);
        GradeHoraria gradeBanco = new GradeHoraria();
        setIdPrivadoGradeHoraria(gradeBanco, 7L);

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turmaBanco));
        when(disciplinaRepository.findById(2L)).thenReturn(Optional.of(disciplinaBanco));
        when(salaRepository.findById(3L)).thenReturn(Optional.of(salaBanco));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(usuarioBanco));
        when(diaSemanaRepository.findById(5L)).thenReturn(Optional.of(diaSemanaBanco));
        when(horarioRepository.findById(6L)).thenReturn(Optional.of(horarioBanco));
        when(gradeHorariaRepository.findById(7L)).thenReturn(Optional.of(gradeBanco));

        Alocacao resultado = useCase.validar(entity);

        assertSame(entity, resultado);
        assertSame(turmaBanco, resultado.getTurma());
        assertSame(disciplinaBanco, resultado.getDisciplina());
        assertSame(salaBanco, resultado.getSala());
        assertSame(usuarioBanco, resultado.getUsuario());
        assertSame(diaSemanaBanco, resultado.getDiaSemana());
        assertSame(horarioBanco, resultado.getHorario());
        assertSame(gradeBanco, resultado.getGradeHoraria());
    }

    @Test
    void deveLancarExcecaoQuandoGradeHorariaNula() {
        ParameterException exception = assertThrows(ParameterException.class, () -> useCase.buscarGradeHorariaPorId(null));
        assertEquals("Grade horaria e obrigatoria.", removerAcentos(exception.getMessage()));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> useCase.buscarUsuarioPorId(999L));
    }

    @Test
    void deveLancarExcecaoQuandoJustificativaForVazia() {
        assertThrows(ParameterException.class, () -> useCase.validarJustificativaAlteracao("   "));
    }

    @Test
    void naoDeveLancarExcecaoQuandoJustificativaValida() {
        assertDoesNotThrow(() -> useCase.validarJustificativaAlteracao("Atualizacao necessaria"));
    }

    private void setIdPrivadoGradeHoraria(GradeHoraria gradeHoraria, Long id) {
        try {
            java.lang.reflect.Field idField = GradeHoraria.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(gradeHoraria, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String removerAcentos(String texto) {
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
