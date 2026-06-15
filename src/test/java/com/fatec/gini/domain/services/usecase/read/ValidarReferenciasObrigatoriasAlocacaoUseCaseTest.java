package com.fatec.gini.domain.services.usecase.read;

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
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.infrastructure.repositories.DiaSemanaRepository;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorRepository;
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
    private BlocoHorarioRepository blocoHorarioRepository;

    @Mock
    private QuadroHorarioRepository quadroHorarioRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Test
    void deveValidarReferenciasObrigatoriasComSucesso() {
        Turma turmaEntrada = new Turma();
        turmaEntrada.setId(1L);
        Disciplina disciplinaEntrada = new Disciplina();
        disciplinaEntrada.setId(2L);
        Sala salaEntrada = new Sala();
        salaEntrada.setId(3L);
        Professor professorEntrada = new Professor();
        professorEntrada.setId(4L);
        DiaSemana diaSemanaEntrada = new DiaSemana();
        diaSemanaEntrada.setId(5L);
        BlocoHorario blocoHorarioEntrada = new BlocoHorario();
        blocoHorarioEntrada.setId(6L);
        QuadroHorario quadroEntrada = new QuadroHorario();
        setIdPrivadoQuadroHoraria(quadroEntrada, 7L);

        Alocacao entity = new Alocacao();
        entity.setTurma(turmaEntrada);
        entity.setDisciplina(disciplinaEntrada);
        entity.setSala(salaEntrada);
        entity.setProfessor(professorEntrada);
        entity.setDiaSemana(diaSemanaEntrada);
        entity.setBlocoHorario(blocoHorarioEntrada);
        entity.setQuadroHorario(quadroEntrada);

        Turma turmaBanco = new Turma();
        turmaBanco.setId(1L);
        Disciplina disciplinaBanco = new Disciplina();
        disciplinaBanco.setId(2L);
        Sala salaBanco = new Sala();
        salaBanco.setId(3L);
        Usuario professorBanco = new Usuario();
        professorBanco.setId(4L);
        DiaSemana diaSemanaBanco = new DiaSemana();
        diaSemanaBanco.setId(5L);
        BlocoHorario blocoHorarioBanco = new BlocoHorario();
        blocoHorarioBanco.setId(6L);
        QuadroHorario quadroBanco = new QuadroHorario();
        setIdPrivadoQuadroHoraria(quadroBanco, 7L);

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turmaBanco));
        when(disciplinaRepository.findById(2L)).thenReturn(Optional.of(disciplinaBanco));
        when(salaRepository.findById(3L)).thenReturn(Optional.of(salaBanco));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(professorBanco));
        when(diaSemanaRepository.findById(5L)).thenReturn(Optional.of(diaSemanaBanco));
        when(blocoHorarioRepository.findById(6L)).thenReturn(Optional.of(blocoHorarioBanco));
        when(quadroHorarioRepository.findById(7L)).thenReturn(Optional.of(quadroBanco));

        Alocacao resultado = useCase.validar(entity);

        assertSame(entity, resultado);
        assertSame(turmaBanco, resultado.getTurma());
        assertSame(disciplinaBanco, resultado.getDisciplina());
        assertSame(salaBanco, resultado.getSala());
        assertSame(professorBanco, resultado.getProfessor());
        assertSame(diaSemanaBanco, resultado.getDiaSemana());
        assertSame(blocoHorarioBanco, resultado.getBlocoHorario());
        assertSame(quadroBanco, resultado.getQuadroHorario());
    }

    @Test
    void deveLancarExcecaoQuandoQuadroHorarioNula() {
        ParameterException exception = assertThrows(ParameterException.class, () -> useCase.buscarQuadroHorarioPorId(null));
        assertEquals("Quadro horário e obrigatoria.", removerAcentos(exception.getMessage()));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(professorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> useCase.buscarProfessorPorId(999L));
    }

    @Test
    void deveLancarExcecaoQuandoJustificativaForVazia() {
        assertThrows(ParameterException.class, () -> useCase.validarJustificativaAlteracao("   "));
    }

    @Test
    void naoDeveLancarExcecaoQuandoJustificativaValida() {
        assertDoesNotThrow(() -> useCase.validarJustificativaAlteracao("Atualizacao necessaria"));
    }

    private void setIdPrivadoQuadroHoraria(QuadroHorario quadroHorario, Long id) {
        try {
            java.lang.reflect.Field idField = QuadroHorario.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(quadroHorario, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String removerAcentos(String texto) {
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
