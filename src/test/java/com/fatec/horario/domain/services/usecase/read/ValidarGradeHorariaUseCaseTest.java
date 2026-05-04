package com.fatec.horario.domain.services.usecase.read;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.PeriodoLetivo;
import com.fatec.horario.domain.entities.Status;
import com.fatec.horario.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.horario.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ValidarGradeHorariaUseCaseTest {

    @InjectMocks
    private ValidarGradeHorariaUseCase useCase;

    @Mock
    private GradeHorariaRepository gradeHorariaRepository;

    @Test
    void naoDeveLancarExcecaoQuandoGradeAtivaEUltimaVersao() {
        GradeHoraria grade = criarGradeHoraria(10L, 1L, 2L, Status.ATIVO);
        Alocacao alocacao = new Alocacao();
        alocacao.setGradeHoraria(grade);

        when(gradeHorariaRepository.findTopByCursoIdAndPeriodoLetivoIdOrderByVersaoDesc(1L, 2L))
                .thenReturn(Optional.of(criarGradeHoraria(10L, 1L, 2L, Status.ATIVO)));

        assertDoesNotThrow(() -> useCase.executar(alocacao));
    }

    @Test
    void deveLancarExcecaoQuandoGradeInativa() {
        GradeHoraria grade = criarGradeHoraria(10L, 1L, 2L, Status.INATIVO);
        Alocacao alocacao = new Alocacao();
        alocacao.setGradeHoraria(grade);

        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
    }

    @Test
    void deveLancarExcecaoQuandoNaoForUltimaVersao() {
        GradeHoraria gradeAtual = criarGradeHoraria(10L, 1L, 2L, Status.ATIVO);
        Alocacao alocacao = new Alocacao();
        alocacao.setGradeHoraria(gradeAtual);

        when(gradeHorariaRepository.findTopByCursoIdAndPeriodoLetivoIdOrderByVersaoDesc(1L, 2L))
                .thenReturn(Optional.of(criarGradeHoraria(11L, 1L, 2L, Status.ATIVO)));

        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrarUltimaVersao() {
        GradeHoraria gradeAtual = criarGradeHoraria(10L, 1L, 2L, Status.ATIVO);
        Alocacao alocacao = new Alocacao();
        alocacao.setGradeHoraria(gradeAtual);

        when(gradeHorariaRepository.findTopByCursoIdAndPeriodoLetivoIdOrderByVersaoDesc(1L, 2L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
    }

    private GradeHoraria criarGradeHoraria(Long id, Long cursoId, Long periodoId, Status status) {
        GradeHoraria gradeHoraria = new GradeHoraria();
        Curso curso = new Curso();
        curso.setId(cursoId);
        PeriodoLetivo periodoLetivo = new PeriodoLetivo();
        periodoLetivo.setId(periodoId);

        gradeHoraria.setCurso(curso);
        gradeHoraria.setPeriodoLetivo(periodoLetivo);
        gradeHoraria.setStatus(status);

        try {
            java.lang.reflect.Field idField = GradeHoraria.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(gradeHoraria, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return gradeHoraria;
    }
}
