package com.fatec.horario.domain.services.usecase.write;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.PeriodoLetivo;
import com.fatec.horario.domain.entities.Status;
import com.fatec.horario.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.horario.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ValidacaoGradeHorariaUseCaseTest {

    @Mock
    private GradeHorariaRepository gradeHorariaRepository;

    @InjectMocks
    private ValidacaoGradeHorariaUseCase validacaoGradeHorariaUseCase;

    @Test
    void deveLancarExcecaoQuandoGradeNaoEstiverAtiva() {
        GradeHoraria gradeHoraria = criarGradeHoraria(1L, 1, Status.INATIVO, 10L, 20L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> validacaoGradeHorariaUseCase.validarGradeHorariaAtivaEUltimaVersao(gradeHoraria));

        assertEquals("A grade horária informada deve estar ativa.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoGradeNaoForUltimaVersao() {
        GradeHoraria gradeHoraria = criarGradeHoraria(1L, 1, Status.ATIVO, 10L, 20L);
        when(gradeHorariaRepository.findTopByCursoIdAndPeriodoLetivoIdOrderByVersaoDesc(10L, 20L))
                .thenReturn(java.util.Optional.of(criarGradeHoraria(2L, 2, Status.ATIVO, 10L, 20L)));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> validacaoGradeHorariaUseCase.validarGradeHorariaAtivaEUltimaVersao(gradeHoraria));

        assertEquals("A grade horária informada não é a última versão disponível.", exception.getMessage());
    }

    @Test
    void naoDeveLancarExcecaoQuandoGradeForAtivaEUltimaVersao() {
        GradeHoraria gradeHoraria = criarGradeHoraria(2L, 2, Status.ATIVO, 10L, 20L);
        when(gradeHorariaRepository.findTopByCursoIdAndPeriodoLetivoIdOrderByVersaoDesc(10L, 20L))
                .thenReturn(java.util.Optional.of(gradeHoraria));

        assertDoesNotThrow(() -> validacaoGradeHorariaUseCase.validarGradeHorariaAtivaEUltimaVersao(gradeHoraria));
    }

    private GradeHoraria criarGradeHoraria(Long id, Integer versao, Status status, Long cursoId, Long periodoLetivoId) {
        GradeHoraria gradeHoraria = new GradeHoraria();
        gradeHoraria.setVersao(versao);
        gradeHoraria.setDataCriacao(LocalDateTime.now());
        gradeHoraria.setStatus(status);

        Curso curso = new Curso();
        curso.setId(cursoId);
        gradeHoraria.setCurso(curso);

        PeriodoLetivo periodoLetivo = new PeriodoLetivo();
        periodoLetivo.setId(periodoLetivoId);
        gradeHoraria.setPeriodoLetivo(periodoLetivo);

        try {
            java.lang.reflect.Field field = GradeHoraria.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(gradeHoraria, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }

        return gradeHoraria;
    }
}
