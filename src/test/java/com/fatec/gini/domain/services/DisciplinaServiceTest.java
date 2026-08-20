package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.TipoSala;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.DisciplinaService;
import com.fatec.gini.domain.services.usecase.read.ValidarDisciplinaSemVinculosUseCase;
import com.fatec.gini.dto.disciplina.DisciplinaRequest;
import com.fatec.gini.dto.disciplina.DisciplinaResponse;
import com.fatec.gini.dto.id.LongDTO;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.TipoSalaRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class DisciplinaServiceTest {

    @Mock
    private DisciplinaRepository repository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private TipoSalaRepository tipoSalaRepository;

    @Mock
    private ValidarDisciplinaSemVinculosUseCase validarDisciplinaSemVinculos;

    @InjectMocks
    private DisciplinaService disciplinaService;

    @Test
    void deveCriarDisciplinaComCursoETipoSalaValidos() {
        LongDTO cursoId = new LongDTO(1L);
        LongDTO tipoSalaId = new LongDTO(2L);

        DisciplinaRequest request = new DisciplinaRequest(
                "Programação Orientada a Objetos", 80, "Obrigatória", 3,
                "Presencial", "POO", "#FF0000", cursoId, tipoSalaId);

        Curso curso = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        curso.setId(1L);

        TipoSala tipoSala = new TipoSala("Laboratório");
        tipoSala.setId(2L);

        Disciplina disciplinaSalva = new Disciplina("Programação Orientada a Objetos", 80, "Obrigatória", 3,
                "Presencial", "POO", "#FF0000");
        disciplinaSalva.setId(10L);
        disciplinaSalva.setCurso(curso);
        disciplinaSalva.setTipoSala(tipoSala);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(tipoSalaRepository.findById(2L)).thenReturn(Optional.of(tipoSala));
        when(repository.save(any(Disciplina.class))).thenReturn(disciplinaSalva);

        DisciplinaResponse resultado = disciplinaService.criar(request);

        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.nome()).isEqualTo("Programação Orientada a Objetos");
        assertThat(resultado.curso().id()).isEqualTo(1L);
    }

    @Test
    void deveLancarExcecaoQuandoCursoNaoExisteAoCriarDisciplina() {
        LongDTO cursoId = new LongDTO(99L);
        LongDTO tipoSalaId = new LongDTO(2L);

        DisciplinaRequest request = new DisciplinaRequest(
                "Banco de Dados", 60, "Obrigatória", 2,
                "Presencial", "BD", "#00FF00", cursoId, tipoSalaId);

        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> disciplinaService.criar(request));
    }

    @Test
    void deveLancarExcecaoQuandoTipoSalaNaoExisteAoCriarDisciplina() {
        LongDTO cursoId = new LongDTO(1L);
        LongDTO tipoSalaId = new LongDTO(99L);

        DisciplinaRequest request = new DisciplinaRequest(
                "Estruturas de Dados", 60, "Obrigatória", 2,
                "Presencial", "ED", "#0000FF", cursoId, tipoSalaId);

        Curso curso = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        curso.setId(1L);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(tipoSalaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> disciplinaService.criar(request));
    }

    @Test
    void deveBuscarDisciplinaPorIdExistente() {
        Disciplina disciplina = new Disciplina("Banco de Dados", 60, "Obrigatória", 2,
                "Presencial", "BD", "#00FF00");
        disciplina.setId(5L);

        when(repository.findById(5L)).thenReturn(Optional.of(disciplina));

        DisciplinaResponse resultado = disciplinaService.buscarPorId(5L);

        assertThat(resultado.id()).isEqualTo(5L);
        assertThat(resultado.nome()).isEqualTo("Banco de Dados");
    }

    @Test
    void deveLancarExcecaoQuandoDisciplinaNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> disciplinaService.buscarPorId(99L));
    }

    @Test
    void deveAtualizarDisciplinaExistente() {
        Disciplina disciplinaExistente = new Disciplina("Banco de Dados", 60, "Obrigatória", 2,
                "Presencial", "BD", "#00FF00");
        disciplinaExistente.setId(5L);

        Curso novoCurso = new Curso("Engenharia de Software", "Semestral", Status.ATIVO, 8);
        novoCurso.setId(2L);

        TipoSala novoTipoSala = new TipoSala("Laboratório");
        novoTipoSala.setId(3L);

        LongDTO cursoId = new LongDTO(2L);
        LongDTO tipoSalaId = new LongDTO(3L);

        DisciplinaRequest requestAtualizado = new DisciplinaRequest(
                "Banco de Dados Avançado", 80, "Optativa", 4,
                "Híbrido", "BDA", "#FFFF00", cursoId, tipoSalaId);

        when(repository.findById(5L)).thenReturn(Optional.of(disciplinaExistente));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(novoCurso));
        when(tipoSalaRepository.findById(3L)).thenReturn(Optional.of(novoTipoSala));
        when(repository.save(any(Disciplina.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DisciplinaResponse resultado = disciplinaService.atualizar(5L, requestAtualizado);

        assertThat(resultado.nome()).isEqualTo("Banco de Dados Avançado");
        assertThat(resultado.cargaHoraria()).isEqualTo(80);
        assertThat(resultado.curso().id()).isEqualTo(2L);
        assertThat(resultado.tipoSala().id()).isEqualTo(3L);
    }

    @Test
    void deveDeletarDisciplinaSemVinculos() {
        when(repository.existsById(5L)).thenReturn(true);

        disciplinaService.deletar(5L);

        verify(repository).deleteById(5L);
    }

    @Test
    void deveLancarExcecaoAoDeletarDisciplinaInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> disciplinaService.deletar(99L));

        verify(validarDisciplinaSemVinculos, never()).validar(99L);
    }

    @Test
    void deveBloquearExclusaoQuandoDisciplinaTemVinculos() {
        when(repository.existsById(5L)).thenReturn(true);

        doThrow(new RuntimeException("Disciplina possui vínculos"))
                .when(validarDisciplinaSemVinculos).validar(5L);

        assertThrows(RuntimeException.class, () -> disciplinaService.deletar(5L));

        verify(repository, never()).deleteById(5L);
    }
}