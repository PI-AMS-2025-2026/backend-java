package com.fatec.horario.domain.services;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.domain.entities.Status;
import com.fatec.horario.domain.entities.TipoSala;
import com.fatec.horario.domain.services.usecase.read.ValidarDisciplinaSemVinculosUseCase;
import com.fatec.horario.dto.disciplina.DisciplinaRequest;
import com.fatec.horario.dto.disciplina.DisciplinaResponse;
import com.fatec.horario.dto.id.LongDTO;
import com.fatec.horario.infrastructure.repositories.CursoRepository;
import com.fatec.horario.infrastructure.repositories.DisciplinaRepository;
import com.fatec.horario.infrastructure.repositories.TipoSalaRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class DisciplinaServiceTest {

    // Precisamos mockar TODOS os repositórios/dependências que o
    // DisciplinaService usa — ele consulta Curso e TipoSala antes de salvar,
    // e usa um UseCase separado para validar vínculos antes de deletar
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
        // ---------- Arrange ----------

        // LongDTO é o "envelope" usado no DisciplinaRequest só pra carregar
        // um ID (curso e tipoSala vêm assim: { "id": 1 })
        LongDTO cursoId = new LongDTO(1L);
        LongDTO tipoSalaId = new LongDTO(2L);

        DisciplinaRequest request = new DisciplinaRequest(
                "Programação Orientada a Objetos", 80, "Obrigatória", 3,
                "Presencial", "POO", "#FF0000", cursoId, tipoSalaId);

        // Simula o Curso e o TipoSala já existentes no banco
        Curso curso = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        curso.setId(1L);

        TipoSala tipoSala = new TipoSala("Laboratório");
        tipoSala.setId(2L);

        Disciplina disciplinaSalva = new Disciplina("Programação Orientada a Objetos", 80, "Obrigatória", 3,
                "Presencial", "POO", "#FF0000");
        disciplinaSalva.setId(10L);
        disciplinaSalva.setCurso(curso);
        disciplinaSalva.setTipoSala(tipoSala);

        // Precisamos configurar CADA mock que o método vai chamar, na ordem
        // em que o service usa eles:
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(tipoSalaRepository.findById(2L)).thenReturn(Optional.of(tipoSala));
        when(repository.save(any(Disciplina.class))).thenReturn(disciplinaSalva);

        // ---------- Act ----------
        DisciplinaResponse resultado = disciplinaService.criar(request);

        // ---------- Assert ----------
        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.nome()).isEqualTo("Programação Orientada a Objetos");
        assertThat(resultado.curso().id()).isEqualTo(1L);
    }

    @Test
    void deveLancarExcecaoQuandoCursoNaoExisteAoCriarDisciplina() {
        // ---------- Arrange ----------

        LongDTO cursoId = new LongDTO(99L);
        LongDTO tipoSalaId = new LongDTO(2L);

        DisciplinaRequest request = new DisciplinaRequest(
                "Banco de Dados", 60, "Obrigatória", 2,
                "Presencial", "BD", "#00FF00", cursoId, tipoSalaId);

        // Simula que o curso com ID 99 não existe
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        // ---------- Act + Assert ----------

        // O service busca o Curso ANTES do TipoSala — então nem chegamos a
        // mockar o tipoSalaRepository aqui, porque o método nunca alcança essa linha
        assertThrows(EntityNotFoundException.class, () -> disciplinaService.criar(request));
    }

    @Test
    void deveLancarExcecaoQuandoTipoSalaNaoExisteAoCriarDisciplina() {
        // ---------- Arrange ----------

        LongDTO cursoId = new LongDTO(1L);
        LongDTO tipoSalaId = new LongDTO(99L);

        DisciplinaRequest request = new DisciplinaRequest(
                "Estruturas de Dados", 60, "Obrigatória", 2,
                "Presencial", "ED", "#0000FF", cursoId, tipoSalaId);

        Curso curso = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        curso.setId(1L);

        // Dessa vez o curso EXISTE...
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        // ...mas o tipo de sala NÃO existe
        when(tipoSalaRepository.findById(99L)).thenReturn(Optional.empty());

        // ---------- Act + Assert ----------
        assertThrows(EntityNotFoundException.class, () -> disciplinaService.criar(request));
    }

    @Test
    void deveBuscarDisciplinaPorIdExistente() {
        // ---------- Arrange ----------

        Disciplina disciplina = new Disciplina("Banco de Dados", 60, "Obrigatória", 2,
                "Presencial", "BD", "#00FF00");
        disciplina.setId(5L);

        when(repository.findById(5L)).thenReturn(Optional.of(disciplina));

        // ---------- Act ----------
        DisciplinaResponse resultado = disciplinaService.buscarPorId(5L);

        // ---------- Assert ----------
        assertThat(resultado.id()).isEqualTo(5L);
        assertThat(resultado.nome()).isEqualTo("Banco de Dados");
    }

    @Test
    void deveLancarExcecaoQuandoDisciplinaNaoExiste() {
        // ---------- Arrange ----------
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ---------- Act + Assert ----------
        assertThrows(EntityNotFoundException.class, () -> disciplinaService.buscarPorId(99L));
    }

    @Test
    void deveAtualizarDisciplinaExistente() {
        // ---------- Arrange ----------

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

        // ---------- Act ----------
        DisciplinaResponse resultado = disciplinaService.atualizar(5L, requestAtualizado);

        // ---------- Assert ----------
        assertThat(resultado.nome()).isEqualTo("Banco de Dados Avançado");
        assertThat(resultado.cargaHoraria()).isEqualTo(80);
        assertThat(resultado.curso().id()).isEqualTo(2L);
        assertThat(resultado.tipoSala().id()).isEqualTo(3L);
    }

    @Test
    void deveDeletarDisciplinaSemVinculos() {
        // ---------- Arrange ----------

        when(repository.existsById(5L)).thenReturn(true);
        // validarDisciplinaSemVinculos.validar(5L) não lança nada = disciplina está livre

        // ---------- Act ----------
        disciplinaService.deletar(5L);

        // ---------- Assert ----------

        // Aqui usamos verify() em vez de assertThat() — porque deletar() é void
        // e não altera nenhum objeto que possamos inspecionar depois.
        // verify() confirma que um determinado método FOI CHAMADO.
        verify(repository).deleteById(5L);
    }

    @Test
    void deveLancarExcecaoAoDeletarDisciplinaInexistente() {
        // ---------- Arrange ----------
        when(repository.existsById(99L)).thenReturn(false);

        // ---------- Act + Assert ----------
        assertThrows(EntityNotFoundException.class, () -> disciplinaService.deletar(99L));

        // Confirma que, como a disciplina nem existe, a validação de vínculos
        // NUNCA chegou a ser chamada — o método parou antes disso
        verify(validarDisciplinaSemVinculos, never()).validar(99L);
    }

    @Test
    void deveBloquearExclusaoQuandoDisciplinaTemVinculos() {
        // ---------- Arrange ----------

        when(repository.existsById(5L)).thenReturn(true);

        // Simula que a validação de vínculos ENCONTROU um vínculo e lançou erro.
        // doThrow() é usado (em vez de when().thenThrow()) porque o método
        // validar() é void — when() não funciona bem com métodos void.
        doThrow(new RuntimeException("Disciplina possui vínculos"))
                .when(validarDisciplinaSemVinculos).validar(5L);

        // ---------- Act + Assert ----------
        assertThrows(RuntimeException.class, () -> disciplinaService.deletar(5L));

        // Confirma que, por causa do vínculo, a exclusão NUNCA aconteceu
        verify(repository, never()).deleteById(5L);
    }
}