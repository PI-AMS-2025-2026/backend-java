package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.dto.disponibilidadeProfessor.GradeDisponibilidadeProfessorRequest;
import com.fatec.gini.dto.disponibilidadeProfessor.HorarioDisponibilidadeProfessorRequest;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorDisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorRepository;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;

@ExtendWith(MockitoExtension.class)
class DisponibilidadeProfessorServiceGradeTest {

    @Mock private DisponibilidadeProfessorRepository repository;
    @Mock private ProfessorRepository professorRepository;
    @Mock private BlocoHorarioRepository blocoHorarioRepository;
    @Mock private ProfessorDisciplinaRepository professorDisciplinaRepository;
    @Mock private ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;

    private DisponibilidadeProfessorService service;
    private Professor professor;
    private BlocoHorario blocoManha;
    private BlocoHorario blocoTarde;

    @BeforeEach
    void setUp() {
        service = new DisponibilidadeProfessorService(
                repository, professorRepository, blocoHorarioRepository,
                professorDisciplinaRepository, validarAutorizacaoCurso);
        professor = new Professor("Ana", "ana@example.com", "", Status.ATIVO);
        professor.setId(12L);
        blocoManha = new BlocoHorario(LocalTime.of(8, 0), LocalTime.of(8, 50), 50);
        blocoManha.setId(21L);
        blocoTarde = new BlocoHorario(LocalTime.of(13, 0), LocalTime.of(13, 50), 50);
        blocoTarde.setId(22L);
        when(professorRepository.findById(12L)).thenReturn(Optional.of(professor));
        when(validarAutorizacaoCurso.cursoParaFiltro(null)).thenReturn(null);
    }

    @Test
    void sincronizaGradeCriandoNovosHorariosRemovendoDesmarcadosEPreservandoOsDemais() {
        DisponibilidadeProfessor segundaManha = disponibilidade(DiaSemana.SEGUNDA, blocoManha, 31L);
        DisponibilidadeProfessor sextaTarde = disponibilidade(DiaSemana.SEXTA, blocoTarde, 32L);
        DisponibilidadeProfessor tercaTarde = disponibilidade(DiaSemana.TERÇA, blocoTarde, 33L);
        when(blocoHorarioRepository.findAllById(any())).thenReturn(List.of(blocoManha, blocoTarde));
        when(repository.findByProfessorId(12L)).thenReturn(
                List.of(segundaManha, sextaTarde), List.of(segundaManha, tercaTarde));

        var resultado = service.sincronizarGrade(12L, new GradeDisponibilidadeProfessorRequest(List.of(
                new HorarioDisponibilidadeProfessorRequest(DiaSemana.SEGUNDA, 21L),
                new HorarioDisponibilidadeProfessorRequest(DiaSemana.TERÇA, 22L))));

        verify(repository).deleteAll(argThat(registros -> {
            var iterator = registros.iterator();
            return iterator.hasNext() && iterator.next().equals(sextaTarde) && !iterator.hasNext();
        }));
        verify(repository).saveAll(argThat(registros -> {
            var iterator = registros.iterator();
            if (!iterator.hasNext()) return false;
            DisponibilidadeProfessor criada = iterator.next();
            return !iterator.hasNext()
                    && criada.getDiaSemana() == DiaSemana.TERÇA
                    && criada.getBlocoHorario().getId().equals(22L);
        }));
        assertThat(resultado).hasSize(2);
    }

    @Test
    void rejeitaParDuplicadoAntesDeAlterarRegistros() {
        var request = new GradeDisponibilidadeProfessorRequest(List.of(
                new HorarioDisponibilidadeProfessorRequest(DiaSemana.SEGUNDA, 21L),
                new HorarioDisponibilidadeProfessorRequest(DiaSemana.SEGUNDA, 21L)));

        assertThrows(ResponseStatusException.class, () -> service.sincronizarGrade(12L, request));

        verify(repository, never()).findByProfessorId(12L);
        verify(repository, never()).deleteAll(any());
        verify(repository, never()).saveAll(any());
    }

    private DisponibilidadeProfessor disponibilidade(DiaSemana dia, BlocoHorario bloco, Long id) {
        DisponibilidadeProfessor disponibilidade = new DisponibilidadeProfessor(professor, dia, bloco);
        disponibilidade.setId(id);
        return disponibilidade;
    }
}
