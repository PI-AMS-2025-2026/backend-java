package com.fatec.horario.domain.services.usecase.read;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.Horario;
import com.fatec.gini.domain.services.usecase.read.ValidarCargaHorariaMaximaProfessorUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Validar Carga Horária Máxima Professor Use Case Tests")
class ValidarCargaHorariaMaximaProfessorUseCaseTest {

    @Mock
    private AlocacaoRepository alocacaoRepository;

    @InjectMocks
    private ValidarCargaHorariaMaximaProfessorUseCase validarCargaHorariaUseCase;

    private static final Long PROFESSOR_ID = 1L;
    private static final Long DIA_SEGUNDA = 2L;
   

    @Nested
    @DisplayName("Testes de Validação de Carga Horária Diária")
    class ValidacaoCargaHorariaDiariaTests {

        @Test
        @DisplayName("Deve aceitar carga horária diária dentro do limite (4 horas de 8)")
        void deveAceitarCargaHorariaDentroDoLimite() {
            // Arrange
            List<Alocacao> alocacoes = new ArrayList<>();
            alocacoes.add(criarAlocacao(LocalTime.of(8, 0), LocalTime.of(9, 0))); // 1h
            alocacoes.add(criarAlocacao(LocalTime.of(10, 0), LocalTime.of(12, 0))); // 2h
            alocacoes.add(criarAlocacao(LocalTime.of(13, 0), LocalTime.of(14, 0))); // 1h
            
            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(alocacoes);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(new ArrayList<>());

            // Act & Assert
            assertDoesNotThrow(() -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
        }

        @Test
        @DisplayName("Deve aceitar carga horária diária exatamente no limite (8 horas)")
        void deveAceitarCargaHorariaNoLimiteExato() {
            // Arrange
            List<Alocacao> alocacoes = new ArrayList<>();
            alocacoes.add(criarAlocacao(LocalTime.of(8, 0), LocalTime.of(9, 30))); // 1h30m
            alocacoes.add(criarAlocacao(LocalTime.of(10, 0), LocalTime.of(12, 30))); // 2h30m
            alocacoes.add(criarAlocacao(LocalTime.of(13, 0), LocalTime.of(16, 30))); // 3h30m
            
            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(alocacoes);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(new ArrayList<>());

            // Act & Assert
            assertDoesNotThrow(() -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
        }

        @Test
        @DisplayName("Deve rejeitar carga horária diária acima do limite (9 horas de 8)")
        void deveRejeitarCargaHorariaAcimaDoLimite() {
            // Arrange
            List<Alocacao> alocacoes = new ArrayList<>();
            alocacoes.add(criarAlocacao(LocalTime.of(8, 0), LocalTime.of(9, 0))); // 1h
            alocacoes.add(criarAlocacao(LocalTime.of(10, 0), LocalTime.of(13, 0))); // 3h
            alocacoes.add(criarAlocacao(LocalTime.of(14, 0), LocalTime.of(19, 15))); // 5h15m
            // Total: 9h15m (acima do limite)
            
            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(alocacoes);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(new ArrayList<>());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
            assertEquals("A carga horária máxima diária do professor foi excedida.",
                    exception.getMessage());
        }

        @Test
        @DisplayName("Deve aceitar quando não há aulas no dia")
        void deveAceitarQuandoNaoHaAulas() {
            // Arrange
            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(new ArrayList<>());
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(new ArrayList<>());

            // Act & Assert
            assertDoesNotThrow(() -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
        }

        @Test
        @DisplayName("Deve rejeitar com múltiplas aulas totalizando mais de 8 horas")
        void deveRejeitarComMultiplasAulasExcedendo() {
            // Arrange - 9 aulas de 1 hora cada = 9 horas
            List<Alocacao> alocacoes = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                int hora = 8 + i;
                alocacoes.add(criarAlocacao(
                        LocalTime.of(hora, 0),
                        LocalTime.of(hora + 1, 0)
                ));
            }
            
            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(alocacoes);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(new ArrayList<>());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
            assertEquals("A carga horária máxima diária do professor foi excedida.",
                    exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de Validação de Descanso de 12 Horas")
    class ValidacaoDescansoMinimTests {

        @Test
        @DisplayName("Deve aceitar descanso >= 12 horas entre dias")
        void deveAceitarDescansoMaiorOuIgualA12Horas() {
            // Arrange
            // Última aula do dia anterior terminou às 18:00
            Alocacao aulaAnterior = criarAlocacao(LocalTime.of(17, 0), LocalTime.of(18, 0));
            // Primeira aula do dia atual começa às 07:00 (13 horas depois)
            List<Alocacao> aulasAnterior = new ArrayList<>();
            aulasAnterior.add(aulaAnterior);

            List<Alocacao> aulasAtual = new ArrayList<>();
            aulasAtual.add(criarAlocacao(LocalTime.of(7, 0), LocalTime.of(8, 0)));

            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(aulasAtual);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(aulasAnterior);

            // Act & Assert
            assertDoesNotThrow(() -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
        }

        @Test
        @DisplayName("Deve rejeitar descanso menor que 12 horas entre dias")
        void deveRejeitarDescansoMenorQue12Horas() {
            // Arrange
            // Última aula do dia anterior terminou às 20:00
            Alocacao aulaAnterior = criarAlocacao(LocalTime.of(19, 0), LocalTime.of(20, 0));
            // Primeira aula do dia atual começa às 07:00 (11 horas depois)
            List<Alocacao> aulasAnterior = new ArrayList<>();
            aulasAnterior.add(aulaAnterior);

            List<Alocacao> aulasAtual = new ArrayList<>();
            aulasAtual.add(criarAlocacao(LocalTime.of(7, 0), LocalTime.of(8, 0)));

            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(aulasAtual);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(aulasAnterior);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
            assertEquals("O intervalo mínimo de interjornada de 12h não foi respeitado.",
                    exception.getMessage());
        }

        @Test
        @DisplayName("Deve aceitar quando não há aulas no dia anterior")
        void deveAceitarQuandoNaoHaAulasNoDiaAnterior() {
            // Arrange
            List<Alocacao> aulasAtual = new ArrayList<>();
            aulasAtual.add(criarAlocacao(LocalTime.of(8, 0), LocalTime.of(9, 0)));

            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(aulasAtual);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(new ArrayList<>());

            // Act & Assert
            assertDoesNotThrow(() -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
        }

        @Test
        @DisplayName("Deve aceitar quando não há aulas no dia atual")
        void deveAceitarQuandoNaoHaAulasNoDiaAtual() {
            // Arrange
            List<Alocacao> aulasAnterior = new ArrayList<>();
            aulasAnterior.add(criarAlocacao(LocalTime.of(17, 0), LocalTime.of(18, 0)));

            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(new ArrayList<>());
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(aulasAnterior);

            // Act & Assert
            assertDoesNotThrow(() -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
        }

        @Test
        @DisplayName("Deve calcular corretamente o descanso com wrapping de domingo para sábado")
        void deveCalcularDescansoComWrappingDeDomingoParaSabado() {
            // Arrange - Segunda-feira (DIA_SEGUNDA = 2)
            // Última aula do domingo terminou às 18:00
            Alocacao aulaAnterior = criarAlocacao(LocalTime.of(17, 0), LocalTime.of(18, 0));
            List<Alocacao> aulasAnterior = new ArrayList<>();
            aulasAnterior.add(aulaAnterior);

            List<Alocacao> aulasAtual = new ArrayList<>();
            aulasAtual.add(criarAlocacao(LocalTime.of(7, 0), LocalTime.of(8, 0)));

            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(aulasAtual);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(aulasAnterior);

            // Act & Assert
            assertDoesNotThrow(() -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
        }

        @Test
        @DisplayName("Deve calcular descanso mínimo de 12h exato com sucesso")
        void deveAceitarDescansoExatamente12Horas() {
            // Arrange
            // Última aula do dia anterior terminou às 19:00
            Alocacao aulaAnterior = criarAlocacao(LocalTime.of(18, 0), LocalTime.of(19, 0));
            List<Alocacao> aulasAnterior = new ArrayList<>();
            aulasAnterior.add(aulaAnterior);

            // Primeira aula do dia atual começa às 07:00 (exatamente 12 horas)
            List<Alocacao> aulasAtual = new ArrayList<>();
            aulasAtual.add(criarAlocacao(LocalTime.of(7, 0), LocalTime.of(8, 0)));

            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(aulasAtual);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(aulasAnterior);

            // Act & Assert
            assertDoesNotThrow(() -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
        }

        @Test
        @DisplayName("Deve rejeitar descanso insuficiente mesmo com múltiplas aulas no dia atual")
        void deveRejeitarDescansoInsuficienteComMultiplasAulas() {
            // Arrange
            Alocacao aulaAnterior = criarAlocacao(LocalTime.of(21, 0), LocalTime.of(22, 0));
            List<Alocacao> aulasAnterior = new ArrayList<>();
            aulasAnterior.add(aulaAnterior);

            // Múltiplas aulas começando cedo demais (8 horas depois)
            List<Alocacao> aulasAtual = new ArrayList<>();
            aulasAtual.add(criarAlocacao(LocalTime.of(6, 0), LocalTime.of(7, 0)));
            aulasAtual.add(criarAlocacao(LocalTime.of(8, 0), LocalTime.of(9, 0)));

            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(aulasAtual);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(aulasAnterior);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
            assertEquals("O intervalo mínimo de interjornada de 12h não foi respeitado.",
                    exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de Integração de Ambas as Validações")
    class ValidacaoIntegradaTests {

        @Test
        @DisplayName("Deve validar carga horária E descanso simultaneamente")
        void deveValidarAmbosOsPilares() {
            // Arrange - carga horária dentro do limite e descanso válido
            List<Alocacao> aulasAtual = new ArrayList<>();
            aulasAtual.add(criarAlocacao(LocalTime.of(8, 0), LocalTime.of(9, 0))); // 1h
            aulasAtual.add(criarAlocacao(LocalTime.of(10, 0), LocalTime.of(14, 0))); // 4h
            // Total: 5h (dentro do limite de 8h)

            Alocacao aulaAnterior = criarAlocacao(LocalTime.of(18, 0), LocalTime.of(19, 0));
            List<Alocacao> aulasAnterior = new ArrayList<>();
            aulasAnterior.add(aulaAnterior);

            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(aulasAtual);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(aulasAnterior);

            // Act & Assert
            assertDoesNotThrow(() -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
        }

        @Test
        @DisplayName("Deve falhar na carga horária mesmo com descanso válido")
        void deveFalharNaCargaHorariaMesmoComDescansoValido() {
            // Arrange
            List<Alocacao> aulasAtual = new ArrayList<>();
            aulasAtual.add(criarAlocacao(LocalTime.of(8, 0), LocalTime.of(9, 30))); // 1h30m
            aulasAtual.add(criarAlocacao(LocalTime.of(10, 0), LocalTime.of(17, 30))); // 7h30m
            // Total: 9h (acima do limite de 8h)

            Alocacao aulaAnterior = criarAlocacao(LocalTime.of(18, 0), LocalTime.of(19, 0));
            List<Alocacao> aulasAnterior = new ArrayList<>();
            aulasAnterior.add(aulaAnterior);

            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(aulasAtual);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(aulasAnterior);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
            assertEquals("A carga horária máxima diária do professor foi excedida.",
                    exception.getMessage());
        }

        @Test
        @DisplayName("Deve falhar no descanso mesmo com carga horária válida")
        void deveFalharNoDescansoMesmoComCargaHorariaValida() {
            // Arrange
            List<Alocacao> aulasAtual = new ArrayList<>();
            aulasAtual.add(criarAlocacao(LocalTime.of(8, 0), LocalTime.of(9, 0))); // 1h
            aulasAtual.add(criarAlocacao(LocalTime.of(10, 0), LocalTime.of(14, 0))); // 4h
            // Total: 5h (dentro do limite)

            Alocacao aulaAnterior = criarAlocacao(LocalTime.of(22, 0), LocalTime.of(23, 0));
            List<Alocacao> aulasAnterior = new ArrayList<>();
            aulasAnterior.add(aulaAnterior);

            Long diaAnterior = calcularDiaAnterior(DIA_SEGUNDA);
            when(alocacaoRepository.findByProfessorAndDiaSemana(PROFESSOR_ID, DIA_SEGUNDA))
                    .thenReturn(aulasAtual);
            when(alocacaoRepository.findUltimaAulaDoDia(PROFESSOR_ID, diaAnterior))
                    .thenReturn(aulasAnterior);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> validarCargaHorariaUseCase.validar(PROFESSOR_ID, DIA_SEGUNDA));
            assertEquals("O intervalo mínimo de interjornada de 12h não foi respeitado.",
                    exception.getMessage());
        }
    }

    // Métodos auxiliares
    private Long calcularDiaAnterior(Long diaSemana) {
        return (diaSemana == 1) ? 7 : diaSemana - 1;
    }

    private Alocacao criarAlocacao(LocalTime horaInicio, LocalTime horaFim) {
        Horario horarioTeste = new Horario(horaInicio, horaFim, 60);
        Alocacao alocacaoTeste = new Alocacao();
        alocacaoTeste.setHorario(horarioTeste);
        return alocacaoTeste;
    }
}
