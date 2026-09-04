package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarCopiarQuadroHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCursoAtivoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarPeriodoAtividadeQuadroAtivoUseCase;
import com.fatec.gini.domain.services.usecase.write.CopiarQuadroHorarioUseCase;
import com.fatec.gini.domain.services.usecase.write.ValidarQuadroHorarioValidoUseCase;
import com.fatec.gini.dto.id.LongDTO;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioRequest;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioResponse;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.PeriodoAtividadeQuadroRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

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
    private ValidarCopiarQuadroHorarioUseCase validarCopiarQuadroHorarioUseCase;

    @Mock
    private AlocacaoRepository alocacaoRepository;

    /*
     * O CopiarQuadroHorarioUseCase NÃO é mockado.
     *
     * O objetivo é testar a implementação real da cópia.
     */
    private CopiarQuadroHorarioUseCase copiarQuadroHorarioUseCase;

    @InjectMocks
    private QuadroHorarioService service;

    @BeforeEach
    void configurarCopiarQuadroHorarioUseCase() {

        copiarQuadroHorarioUseCase =
                new CopiarQuadroHorarioUseCase(
                        repository,
                        alocacaoRepository,
                        validarCopiarQuadroHorarioUseCase,
                        validarQuadroHorarioValidoUseCase,
                        validarCursoAtivoUseCase);

        /*
         * Como o service possui o use case de cópia como dependência,
         * precisamos substituir o mock criado pelo @InjectMocks
         * pela implementação real.
         */
        service = new QuadroHorarioService(
                repository,
                cursoRepository,
                periodoRepository,
                validarQuadroHorarioValidoUseCase,
                validarPeriodoAtividadeQuadroAtivoUseCase,
                copiarQuadroHorarioUseCase,
                validarCursoAtivoUseCase);
    }

    // ============================================================
    // CRIAÇÃO
    // ============================================================

    @Test
    void deveLancarExcecaoAoCriarQuadroComCursoInativo() {

        LongDTO cursoDTO = new LongDTO(1L);
        LongDTO periodoDTO = new LongDTO(1L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(
                        1,
                        Status.ATIVO,
                        cursoDTO,
                        periodoDTO);

        Curso cursoInativo =
                new Curso(
                        "ADS",
                        "Semestral",
                        Status.INATIVO,
                        6);

        cursoInativo.setId(1L);

        PeriodoAtividadeQuadro periodo =
                new PeriodoAtividadeQuadro();

        periodo.setId(1L);
        periodo.setStatus(Status.ATIVO);

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(cursoInativo));

        when(periodoRepository.findById(1L))
                .thenReturn(Optional.of(periodo));

        doThrow(new BusinessException(
                "Não é permitido criar ou alterar quadro horário para um curso inativo."))
                .when(validarCursoAtivoUseCase)
                .validar(cursoInativo);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> service.criar(request));

        assertThat(exception.getMessage())
                .contains("curso inativo");

        verify(validarCursoAtivoUseCase)
                .validar(cursoInativo);
    }

    @Test
    void devePermitirCriarQuadroComCursoAtivo() {

        LongDTO cursoDTO = new LongDTO(1L);
        LongDTO periodoDTO = new LongDTO(1L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(
                        1,
                        Status.ATIVO,
                        cursoDTO,
                        periodoDTO);

        Curso cursoAtivo =
                new Curso(
                        "ADS",
                        "Semestral",
                        Status.ATIVO,
                        6);

        cursoAtivo.setId(1L);

        PeriodoAtividadeQuadro periodo =
                new PeriodoAtividadeQuadro();

        periodo.setId(1L);
        periodo.setStatus(Status.ATIVO);

        QuadroHorario quadroSalvo =
                new QuadroHorario(
                        1,
                        Status.ATIVO);

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

        doNothing()
                .when(validarCursoAtivoUseCase)
                .validar(cursoAtivo);

        QuadroHorarioResponse resultado =
                service.criar(request);

        assertThat(resultado.id())
                .isEqualTo(1L);

        assertThat(resultado.status())
                .isEqualTo(Status.ATIVO);

        verify(validarCursoAtivoUseCase)
                .validar(cursoAtivo);

        verify(validarQuadroHorarioValidoUseCase)
                .validarQuadroAtivoDuplicado(
                        any(QuadroHorario.class));
    }

    // ============================================================
    // CRIAÇÃO - STATUS
    // ============================================================

    @Test
    void deveCriarQuadroComoAtivoMesmoQueRequisicaoInformeInativo() {

        LongDTO cursoDTO = new LongDTO(1L);
        LongDTO periodoDTO = new LongDTO(1L);

        /*
         * IMPORTANTE:
         * O request informa INATIVO.
         * A regra de negócio deve obrigar o novo quadro
         * a ser criado como ATIVO.
         */
        QuadroHorarioRequest request =
                new QuadroHorarioRequest(
                        1,
                        Status.INATIVO,
                        cursoDTO,
                        periodoDTO);

        Curso cursoAtivo =
                new Curso(
                        "ADS",
                        "Semestral",
                        Status.ATIVO,
                        6);

        cursoAtivo.setId(1L);

        PeriodoAtividadeQuadro periodo =
                new PeriodoAtividadeQuadro();

        periodo.setId(1L);
        periodo.setStatus(Status.ATIVO);

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(cursoAtivo));

        when(periodoRepository.findById(1L))
                .thenReturn(Optional.of(periodo));

        doNothing()
                .when(validarCursoAtivoUseCase)
                .validar(cursoAtivo);

        when(repository.save(any(QuadroHorario.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        QuadroHorarioResponse resultado =
                service.criar(request);

        /*
         * Mesmo com INATIVO no request,
         * o quadro deve ser persistido como ATIVO.
         */
        assertThat(resultado.status())
                .isEqualTo(Status.ATIVO);
    }

    // ============================================================
    // ATUALIZAÇÃO
    // ============================================================

    @Test
    void deveLancarExcecaoAoAtualizarQuadroComCursoInativo() {

        Long idQuadro = 1L;

        LongDTO cursoDTO = new LongDTO(1L);
        LongDTO periodoDTO = new LongDTO(1L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(
                        2,
                        Status.ATIVO,
                        cursoDTO,
                        periodoDTO);

        QuadroHorario quadroExistente =
                new QuadroHorario(
                        1,
                        Status.ATIVO);

        quadroExistente.setId(idQuadro);

        Curso cursoInativo =
                new Curso(
                        "ADS",
                        "Semestral",
                        Status.INATIVO,
                        6);

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

        doThrow(new BusinessException(
                "Não é permitido criar ou alterar quadro horário para um curso inativo."))
                .when(validarCursoAtivoUseCase)
                .validar(cursoInativo);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> service.atualizar(
                                idQuadro,
                                request));

        assertThat(exception.getMessage())
                .contains("curso inativo");

        verify(validarCursoAtivoUseCase)
                .validar(cursoInativo);
    }

    @Test
    void devePermitirAtualizarQuadroComCursoAtivo() {

        Long idQuadro = 1L;

        LongDTO cursoDTO = new LongDTO(1L);
        LongDTO periodoDTO = new LongDTO(1L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(
                        2,
                        Status.ATIVO,
                        cursoDTO,
                        periodoDTO);

        Curso cursoAtivo =
                new Curso(
                        "ADS",
                        "Semestral",
                        Status.ATIVO,
                        6);

        cursoAtivo.setId(1L);

        PeriodoAtividadeQuadro periodo =
                new PeriodoAtividadeQuadro();

        periodo.setId(1L);
        periodo.setStatus(Status.ATIVO);

        QuadroHorario quadroExistente =
                new QuadroHorario(
                        1,
                        Status.ATIVO);

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

        doNothing()
                .when(validarCursoAtivoUseCase)
                .validar(cursoAtivo);

        service.atualizar(
                idQuadro,
                request);

        verify(validarCursoAtivoUseCase)
                .validar(cursoAtivo);

        verify(validarQuadroHorarioValidoUseCase)
                .validarQuadroAtivoDuplicado(
                        any(QuadroHorario.class));
    }

    // ============================================================
    // ATUALIZAÇÃO DO PRÓPRIO QUADRO
    // ============================================================

    @Test
    void devePermitirAtualizarOProprioQuadroAtivoSemConsideraLoDuplicado() {

        Long idQuadro = 1L;

        Long idCurso = 1L;
        Long idPeriodo = 1L;

        LongDTO cursoDTO =
                new LongDTO(idCurso);

        LongDTO periodoDTO =
                new LongDTO(idPeriodo);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(
                        2,
                        Status.ATIVO,
                        cursoDTO,
                        periodoDTO);

        Curso cursoAtivo =
                new Curso(
                        "ADS",
                        "Semestral",
                        Status.ATIVO,
                        6);

        cursoAtivo.setId(idCurso);

        PeriodoAtividadeQuadro periodo =
                new PeriodoAtividadeQuadro();

        periodo.setId(idPeriodo);
        periodo.setStatus(Status.ATIVO);

        QuadroHorario quadroExistente =
                new QuadroHorario(
                        1,
                        Status.ATIVO);

        quadroExistente.setId(idQuadro);
        quadroExistente.setCurso(cursoAtivo);
        quadroExistente.setPeriodoAtividadeQuadro(periodo);

        when(repository.findById(idQuadro))
                .thenReturn(Optional.of(quadroExistente));

        when(cursoRepository.findById(idCurso))
                .thenReturn(Optional.of(cursoAtivo));

        when(periodoRepository.findById(idPeriodo))
                .thenReturn(Optional.of(periodo));

        when(repository.save(any(QuadroHorario.class)))
                .thenReturn(quadroExistente);

        doNothing()
                .when(validarCursoAtivoUseCase)
                .validar(cursoAtivo);

        service.atualizar(
                idQuadro,
                request);

        /*
         * A validação deve verificar a existência de OUTRO
         * quadro ativo, excluindo o próprio ID.
         *
         * Como o validator está mockado neste teste,
         * verificamos que ele foi executado sobre o quadro
         * que possui o próprio ID.
         */
        verify(validarQuadroHorarioValidoUseCase)
                .validarQuadroAtivoDuplicado(
                        any(QuadroHorario.class));

        verify(repository)
                .save(any(QuadroHorario.class));
    }

    // ============================================================
    // CÓPIA - CURSO INATIVO
    // ============================================================

    @Test
    void deveLancarExcecaoAoCopiarQuadroComCursoInativo() {

        Long idOrigem = 1L;

        LongDTO periodoDTO =
                new LongDTO(2L);

        QuadroHorarioRequest request =
                new QuadroHorarioRequest(
                        1,
                        Status.ATIVO,
                        null,
                        periodoDTO);

        QuadroHorario dadosNovaGrade =
                new QuadroHorario();

        dadosNovaGrade.setVersao(
                request.versao());

        dadosNovaGrade.setPeriodoAtividadeQuadro(
                new PeriodoAtividadeQuadro());

        dadosNovaGrade.getPeriodoAtividadeQuadro()
                .setId(2L);

        Curso cursoInativo =
                new Curso(
                        "ADS",
                        "Semestral",
                        Status.INATIVO,
                        6);

        cursoInativo.setId(1L);

        QuadroHorario quadroOrigem =
                new QuadroHorario(
                        1,
                        Status.ATIVO);

        quadroOrigem.setId(idOrigem);
        quadroOrigem.setCurso(cursoInativo);

        /*
         * Agora o repository é mockado,
         * mas o CopiarQuadroHorarioUseCase é REAL.
         */
        when(repository.findById(idOrigem))
                .thenReturn(Optional.of(quadroOrigem));

        doThrow(new BusinessException(
                "Não é permitido criar ou alterar quadro horário para um curso inativo."))
                .when(validarCursoAtivoUseCase)
                .validar(cursoInativo);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> copiarQuadroHorarioUseCase.executar(
                                idOrigem,
                                dadosNovaGrade));

        assertThat(exception.getMessage())
                .contains("curso inativo");

        verify(validarCursoAtivoUseCase)
                .validar(cursoInativo);
    }

    // ============================================================
    // CÓPIA - CURSO ATIVO
    // ============================================================

    @Test
    void devePermitirCopiarQuadroComCursoAtivo() {

        Long idOrigem = 1L;

        Curso cursoAtivo =
                new Curso(
                        "ADS",
                        "Semestral",
                        Status.ATIVO,
                        6);

        cursoAtivo.setId(1L);

        PeriodoAtividadeQuadro periodoOrigem =
                new PeriodoAtividadeQuadro();

        periodoOrigem.setId(1L);
        periodoOrigem.setStatus(Status.ATIVO);

        PeriodoAtividadeQuadro periodoNovo =
                new PeriodoAtividadeQuadro();

        periodoNovo.setId(2L);
        periodoNovo.setStatus(Status.ATIVO);

        QuadroHorario quadroOrigem =
                new QuadroHorario(
                        1,
                        Status.ATIVO);

        quadroOrigem.setId(idOrigem);
        quadroOrigem.setCurso(cursoAtivo);
        quadroOrigem.setPeriodoAtividadeQuadro(
                periodoOrigem);

        QuadroHorario dadosNovaGrade =
                new QuadroHorario();

        dadosNovaGrade.setVersao(1);
        dadosNovaGrade.setPeriodoAtividadeQuadro(
                periodoNovo);

        when(repository.findById(idOrigem))
                .thenReturn(Optional.of(quadroOrigem));

        /*
         * Como o curso está ativo, a validação não lança exceção.
         */
        doNothing()
                .when(validarCursoAtivoUseCase)
                .validar(cursoAtivo);

        /*
         * Não existem alocações na origem.
         * Para este teste estamos validando a integração da
         * validação do curso com o use case de cópia.
         */
        when(alocacaoRepository.findByQuadroHorarioId(idOrigem))
                .thenReturn(java.util.Collections.emptyList());

        /*
         * A validação existente aceita a lista vazia neste teste.
         */
        doNothing()
                .when(validarCopiarQuadroHorarioUseCase)
                .validarRegrasParaCopiaDeGrade(
                        any());

        /*
         * Não existe outro quadro ativo.
         */
        doNothing()
                .when(validarQuadroHorarioValidoUseCase)
                .validarQuadroAtivoDuplicado(
                        any());

        QuadroHorario quadroSalvo =
                new QuadroHorario(
                        2,
                        Status.ATIVO);

        quadroSalvo.setId(2L);
        quadroSalvo.setCurso(cursoAtivo);
        quadroSalvo.setPeriodoAtividadeQuadro(
                periodoNovo);

        when(repository.save(any(QuadroHorario.class)))
                .thenReturn(quadroSalvo);

        QuadroHorario resultado =
                copiarQuadroHorarioUseCase.executar(
                        idOrigem,
                        dadosNovaGrade);

        assertThat(resultado.getId())
                .isEqualTo(2L);

        assertThat(resultado.getStatus())
                .isEqualTo(Status.ATIVO);

        assertThat(resultado.getVersao())
                .isEqualTo(2);

        assertThat(resultado.getCurso().getStatus())
                .isEqualTo(Status.ATIVO);

        verify(validarCursoAtivoUseCase)
                .validar(cursoAtivo);
    }
}