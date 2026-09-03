package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarCursoAtivoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarPeriodoAtividadeQuadroAtivoUseCase;
import com.fatec.gini.domain.services.usecase.write.CopiarQuadroHorarioUseCase;
import com.fatec.gini.domain.services.usecase.write.ValidarQuadroHorarioValidoUseCase;
import com.fatec.gini.dto.id.LongDTO;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioRequest;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioResponse;
import com.fatec.gini.infrastructure.mappers.QuadroHorarioMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.PeriodoAtividadeQuadroRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class QuadroHorarioServiceTest {

    @Mock
    private QuadroHorarioRepository repository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private PeriodoAtividadeQuadroRepository periodoRepository;

    @Mock
    private ValidarQuadroHorarioValidoUseCase validarQuadroHorarioValidoUseCase;

    @Mock
    private ValidarPeriodoAtividadeQuadroAtivoUseCase validarPeriodoAtividadeQuadroAtivoUseCase;

    @Mock
    private ValidarCursoAtivoUseCase validarCursoAtivoUseCase;

    @Mock
    private CopiarQuadroHorarioUseCase copiarQuadroHorarioUseCase;

    @InjectMocks
    private QuadroHorarioService service;

    // ========== TESTES: CRIAÇÃO COM CURSO INATIVO ==========

    @Test
    void deveLancarExcecaoAoCriarQuadroComCursoInativo() {
        // ---------- Arrange ----------
        LongDTO cursoDTO = new LongDTO(1L);
        LongDTO periodoDTO = new LongDTO(1L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(1, Status.ATIVO, cursoDTO, periodoDTO);

        Curso cursoInativo = new Curso("ADS", "Semestral", Status.INATIVO, 6);
        cursoInativo.setId(1L);

        PeriodoAtividadeQuadro periodo =
                new PeriodoAtividadeQuadro();
        periodo.setId(1L);
        periodo.setStatus(Status.ATIVO);

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(cursoInativo));

        when(periodoRepository.findById(1L))
                .thenReturn(Optional.of(periodo));

        // Simula a validação do usecase lançando exceção
        doThrow(new BusinessException(
                "Não é permitido criar ou alterar quadro horário para um curso inativo."))
                .when(validarCursoAtivoUseCase)
                .validar(cursoInativo);

        // ---------- Act & Assert ----------
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> service.criar(request));

        assertThat(exception.getMessage())
                .contains("curso inativo");

        // Verifica que a validação foi chamada
        verify(validarCursoAtivoUseCase).validar(cursoInativo);
    }

    @Test
    void devePermitirCriarQuadroComCursoAtivo() {
        // ---------- Arrange ----------
        LongDTO cursoDTO = new LongDTO(1L);
        LongDTO periodoDTO = new LongDTO(1L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(1, Status.ATIVO, cursoDTO, periodoDTO);

        Curso cursoAtivo = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        cursoAtivo.setId(1L);

        PeriodoAtividadeQuadro periodo =
                new PeriodoAtividadeQuadro();
        periodo.setId(1L);
        periodo.setStatus(Status.ATIVO);

        QuadroHorario quadroSalvo = new QuadroHorario(1, Status.ATIVO);
        quadroSalvo.setId(1L);
        quadroSalvo.setCurso(cursoAtivo);
        quadroSalvo.setPeriodoAtividadeQuadro(periodo);
        quadroSalvo.setCreatedAt(LocalDateTime.now());
        quadroSalvo.setUpdatedAt(LocalDateTime.now());

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(cursoAtivo));

        when(periodoRepository.findById(1L))
                .thenReturn(Optional.of(periodo));

        when(repository.save(any(QuadroHorario.class)))
                .thenReturn(quadroSalvo);

        // ---------- Act ----------
        QuadroHorarioResponse resultado = service.criar(request);

        // ---------- Assert ----------
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.status()).isEqualTo(Status.ATIVO);

        // Verifica que a validação foi chamada
        verify(validarCursoAtivoUseCase).validar(cursoAtivo);
        verify(validarQuadroHorarioValidoUseCase)
                .validarQuadroAtivoDuplicado(any(QuadroHorario.class));
    }

    // ========== TESTES: ATUALIZAÇÃO COM CURSO INATIVO ==========

    @Test
    void deveLancarExcecaoAoAtualizarQuadroComCursoInativo() {
        // ---------- Arrange ----------
        Long idQuadro = 1L;

        LongDTO cursoDTO = new LongDTO(1L);
        LongDTO periodoDTO = new LongDTO(1L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(2, Status.ATIVO, cursoDTO, periodoDTO);

        QuadroHorario quadroExistente = new QuadroHorario(1, Status.ATIVO);
        quadroExistente.setId(idQuadro);

        Curso cursoInativo = new Curso("ADS", "Semestral", Status.INATIVO, 6);
        cursoInativo.setId(1L);

        PeriodoAtividadeQuadro periodo =
                new PeriodoAtividadeQuadro();
        periodo.setId(1L);
        periodo.setStatus(Status.ATIVO);

        when(repository.findById(idQuadro))
                .thenReturn(Optional.of(quadroExistente));

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(cursoInativo));

        when(periodoRepository.findById(1L))
                .thenReturn(Optional.of(periodo));

        // Simula a validação do usecase lançando exceção
        doThrow(new BusinessException(
                "Não é permitido criar ou alterar quadro horário para um curso inativo."))
                .when(validarCursoAtivoUseCase)
                .validar(cursoInativo);

        // ---------- Act & Assert ----------
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> service.atualizar(idQuadro, request));

        assertThat(exception.getMessage())
                .contains("curso inativo");

        verify(validarCursoAtivoUseCase).validar(cursoInativo);
    }

    @Test
    void devePermitirAtualizarQuadroComCursoAtivo() {
        // ---------- Arrange ----------
        Long idQuadro = 1L;

        LongDTO cursoDTO = new LongDTO(1L);
        LongDTO periodoDTO = new LongDTO(1L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(2, Status.ATIVO, cursoDTO, periodoDTO);

        Curso cursoAtivo = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        cursoAtivo.setId(1L);

        PeriodoAtividadeQuadro periodo =
                new PeriodoAtividadeQuadro();
        periodo.setId(1L);
        periodo.setStatus(Status.ATIVO);

        QuadroHorario quadroExistente = new QuadroHorario(1, Status.ATIVO);
        quadroExistente.setId(idQuadro);
        quadroExistente.setCurso(cursoAtivo);
        quadroExistente.setPeriodoAtividadeQuadro(periodo);
        quadroExistente.setCreatedAt(LocalDateTime.now());
        quadroExistente.setUpdatedAt(LocalDateTime.now());

        when(repository.findById(idQuadro))
                .thenReturn(Optional.of(quadroExistente));

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(cursoAtivo));

        when(periodoRepository.findById(1L))
                .thenReturn(Optional.of(periodo));

        when(repository.save(any(QuadroHorario.class)))
                .thenReturn(quadroExistente);

        // ---------- Act ----------
        QuadroHorarioResponse resultado = service.atualizar(idQuadro, request);

        // ---------- Assert ----------
        assertThat(resultado.id()).isEqualTo(idQuadro);
        assertThat(resultado.versao()).isEqualTo(2);

        verify(validarCursoAtivoUseCase).validar(cursoAtivo);
        verify(validarQuadroHorarioValidoUseCase)
                .validarQuadroAtivoDuplicado(any(QuadroHorario.class));
    }

    // ========== TESTES: CÓPIA COM CURSO INATIVO ==========

    @Test
    void deveLancarExcecaoAoCopiarQuadroComCursoInativo() {
        // ---------- Arrange ----------
        Long idOrigemInvalido = 1L;

        LongDTO periodoDTO = new LongDTO(1L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(1, Status.ATIVO, null, periodoDTO);

        QuadroHorario quadroOrigem = new QuadroHorario(1, Status.ATIVO);
        quadroOrigem.setId(idOrigemInvalido);

        Curso cursoInativo = new Curso("ADS", "Semestral", Status.INATIVO, 6);
        cursoInativo.setId(1L);

        quadroOrigem.setCurso(cursoInativo);

        // Simula a validação do usecase lançando exceção
        when(copiarQuadroHorarioUseCase.executar(idOrigemInvalido, 
                QuadroHorarioMapper.toEntity(request)))
                .thenThrow(new BusinessException(
                        "Não é permitido criar ou alterar quadro horário para um curso inativo."));

        // ---------- Act & Assert ----------
        // A exceção será lançada pelo CopiarQuadroHorarioUseCase
        // que valida o curso antes de criar a cópia
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> copiarQuadroHorarioUseCase.executar(
                                idOrigemInvalido,
                                QuadroHorarioMapper.toEntity(request)));

        assertThat(exception.getMessage())
                .contains("curso inativo");
    }

    @Test
    void devePermitirCopiarQuadroComCursoAtivo() {
        // ---------- Arrange ----------
        Long idOrigem = 1L;

        LongDTO periodoDTO = new LongDTO(2L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(1, Status.ATIVO, null, periodoDTO);

        Curso cursoAtivo = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        cursoAtivo.setId(1L);

        PeriodoAtividadeQuadro periodoOrigem =
                new PeriodoAtividadeQuadro();
        periodoOrigem.setId(1L);
        periodoOrigem.setStatus(Status.ATIVO);

        PeriodoAtividadeQuadro periodoNovo =
                new PeriodoAtividadeQuadro();
        periodoNovo.setId(2L);
        periodoNovo.setStatus(Status.ATIVO);

        QuadroHorario quadroOrigem = new QuadroHorario(1, Status.ATIVO);
        quadroOrigem.setId(idOrigem);
        quadroOrigem.setCurso(cursoAtivo);
        quadroOrigem.setPeriodoAtividadeQuadro(periodoOrigem);

        QuadroHorario quadroCopia = new QuadroHorario(2, Status.ATIVO);
        quadroCopia.setId(2L);
        quadroCopia.setCurso(cursoAtivo);
        quadroCopia.setPeriodoAtividadeQuadro(periodoNovo);
        quadroCopia.setCreatedAt(LocalDateTime.now());
        quadroCopia.setUpdatedAt(LocalDateTime.now());
        quadroCopia.setDataCriacao(LocalDateTime.now());

        when(copiarQuadroHorarioUseCase.executar(
                idOrigem,
                QuadroHorarioMapper.toEntity(request)))
                .thenReturn(quadroCopia);

        // ---------- Act ----------
        QuadroHorario resultado = copiarQuadroHorarioUseCase.executar(
                idOrigem,
                QuadroHorarioMapper.toEntity(request));

        // ---------- Assert ----------
        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(resultado.getVersao()).isEqualTo(2);
        assertThat(resultado.getCurso().getStatus()).isEqualTo(Status.ATIVO);
    }

    // ========== TESTE: CRIAÇÃO COM STATUS EXPLICITAMENTE ATIVO ==========

    @Test
    void deveCriarQuadroComStatusAtivoIndependentementeDaRequisicao() {
        // ---------- Arrange ----------
        // Mesmo que o request tente usar outro status, o service força como ATIVO
        LongDTO cursoDTO = new LongDTO(1L);
        LongDTO periodoDTO = new LongDTO(1L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(1, Status.ATIVO, cursoDTO, periodoDTO);

        Curso cursoAtivo = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        cursoAtivo.setId(1L);

        PeriodoAtividadeQuadro periodo =
                new PeriodoAtividadeQuadro();
        periodo.setId(1L);
        periodo.setStatus(Status.ATIVO);

        QuadroHorario quadroSalvo = new QuadroHorario(1, Status.ATIVO);
        quadroSalvo.setId(1L);
        quadroSalvo.setCurso(cursoAtivo);
        quadroSalvo.setPeriodoAtividadeQuadro(periodo);
        quadroSalvo.setStatus(Status.ATIVO); // Sempre ATIVO
        quadroSalvo.setCreatedAt(LocalDateTime.now());
        quadroSalvo.setUpdatedAt(LocalDateTime.now());

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(cursoAtivo));

        when(periodoRepository.findById(1L))
                .thenReturn(Optional.of(periodo));

        when(repository.save(any(QuadroHorario.class)))
                .thenReturn(quadroSalvo);

        // ---------- Act ----------
        QuadroHorarioResponse resultado = service.criar(request);

        // ---------- Assert ----------
        // Verifica que o status foi atribuído como ATIVO
        assertThat(resultado.status()).isEqualTo(Status.ATIVO);
    }
}
