package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;
import com.fatec.gini.dto.curso.CursoRequest;
import com.fatec.gini.dto.curso.CursoResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.infrastructure.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository repository;

    @Mock
    private ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;

    @InjectMocks
    private CursoService cursoService;

    @Test
    void deveCriarCursoComDadosValidos() {
        // ---------- Arrange ----------
        CursoRequest request = new CursoRequest("ADS", "Semestral", Status.ATIVO, 6);

        Curso cursoSalvo = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        cursoSalvo.setId(1L);

        when(repository.save(any(Curso.class))).thenReturn(cursoSalvo);

        // ---------- Act ----------
        CursoResponse resultado = cursoService.criar(request);

        // ---------- Assert ----------
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("ADS");
        assertThat(resultado.status()).isEqualTo(Status.ATIVO);
    }

    @Test
    void deveLancarExcecaoQuandoCursoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cursoService.buscarPorId(99L));
    }

    @Test
    void deveAtualizarCursoExistente() {
        // ---------- Arrange ----------
        Curso cursoExistente = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        cursoExistente.setId(1L);

        CursoRequest requestAtualizado = new CursoRequest("Engenharia de Software", "Anual", Status.ATIVO, 8);

        when(repository.findById(1L)).thenReturn(Optional.of(cursoExistente));
        when(repository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ---------- Act ----------
        CursoResponse resultado = cursoService.atualizar(1L, requestAtualizado);

        // ---------- Assert ----------
        assertThat(resultado.nome()).isEqualTo("Engenharia de Software");
        assertThat(resultado.periodicidade()).isEqualTo("Anual");
        assertThat(resultado.duracao()).isEqualTo(8);
    }

    @Test
    void deveLancarExcecaoAoAtualizarCursoInexistente() {
        CursoRequest requestAtualizado = new CursoRequest("Qualquer Nome", "Anual", Status.ATIVO, 8);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cursoService.atualizar(99L, requestAtualizado));
    }

    @Test
    void deveInativarCursoAlterandoStatusSemExcluir() {
        Curso cursoExistente = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        cursoExistente.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(cursoExistente));

        cursoService.inativar(1L);

        assertThat(cursoExistente.getStatus()).isEqualTo(Status.INATIVO);
    }

    @Test
    void deveListarCursosComFiltrosAplicados() {
        // ---------- Arrange ----------
        Curso curso1 = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        curso1.setId(1L);
        Curso curso2 = new Curso("Engenharia de Software", "Semestral", Status.ATIVO, 6);
        curso2.setId(2L);

        // O repositório ainda devolve um Page<Curso> do Spring Data normalmente —
        // é só o retorno do SERVICE que virou PageResponse, não o do repositório.
        Page<Curso> paginaSimulada = new PageImpl<>(List.of(curso1, curso2));

        when(repository.buscarPorFiltros(any(), any(), any(), any(), any(), any(PageRequest.class)))
                .thenReturn(paginaSimulada);

        // ---------- Act ----------
        PageResponse<CursoResponse> resultado = cursoService.listar(null, null, null, null, 0, 10);

        // ---------- Assert ----------
        // PageResponse é um record, então os campos são acessados como métodos:
        // content(), page(), size(), totalElements(), totalPages()
        assertThat(resultado.content()).hasSize(2);
        assertThat(resultado.content().get(0).nome()).isEqualTo("ADS");
        assertThat(resultado.content().get(1).nome()).isEqualTo("Engenharia de Software");
    }
}