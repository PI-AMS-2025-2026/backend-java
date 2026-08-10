package com.fatec.gini.domain.services.usecase.read;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarCargaHorariaMaximaProfessorUseCase {

        private final AlocacaoRepository alocacaoRepository;

        // Limite máximo de horas que um professor pode lecionar em um único dia
        private static final Duration LIMITE_CARGA_HORARIA_DIARIA = Duration.ofHours(8);

        // Intervalo mínimo obrigatório entre o fim de uma jornada e o início da próxima
        private static final Duration DESCANSO_MINIMO_INTERJORNADA = Duration.ofHours(12);

        // Quantidade de minutos existentes em um dia completo
        private static final long MINUTOS_POR_DIA = Duration.ofDays(1).toMinutes();

        @Transactional(readOnly = true)
        public void validar(Long professorId, DiaSemana diaSemana) {

                // Valida se o professor ultrapassou a carga horária diária permitida
                validarCargaHorariaDiaria(professorId, diaSemana);

                // Valida se existe pelo menos 12h de descanso entre jornadas
                validarDescansoMinimo12Horas(professorId, diaSemana);
        }

        /**
         * Regra de negócio:
         * Um professor não pode ultrapassar 8 horas de aula em um único dia.
         */
        private void validarCargaHorariaDiaria(Long professorId, DiaSemana diaSemana) {

                List<Alocacao> alocacoes = alocacaoRepository.findByProfessorIdAndDiaSemana(professorId, diaSemana);

                // Soma a duração de todos os blocos de horário do dia
                long totalMinutos = alocacoes.stream()
                                .mapToLong(alocacao -> Duration.between(
                                                alocacao.getBlocoHorario().getHoraInicio(),
                                                alocacao.getBlocoHorario().getHoraFim())
                                                .toMinutes())
                                .sum();

                if (totalMinutos > LIMITE_CARGA_HORARIA_DIARIA.toMinutes()) {
                        throw new BusinessException(
                                        "A carga horária máxima diária do professor foi excedida.");
                }
        }

        /**
         * Regra de negócio:
         * Deve existir no mínimo 12 horas de descanso entre o término da última aula
         * de um dia e o início da primeira aula do dia seguinte.
         */
        private void validarDescansoMinimo12Horas(Long professorId, DiaSemana diaSemana) {

                // Obtém o dia anterior utilizando o próprio enum
                DiaSemana diaAnterior = diaSemana.anterior();

                // Busca a última aula do dia anterior
                List<Alocacao> aulasDiaAnterior = alocacaoRepository.buscarUltimaAulaDoDia(professorId, diaAnterior);

                // Busca todas as aulas do dia atual
                List<Alocacao> aulasDiaAtual = alocacaoRepository.findByProfessorIdAndDiaSemana(professorId, diaSemana);

                // Se não houver aulas em um dos dias não existe restrição de descanso
                if (aulasDiaAnterior.isEmpty() || aulasDiaAtual.isEmpty()) {
                        return;
                }

                // Considera a última aula do dia anterior
                LocalTime fimDiaAnterior = aulasDiaAnterior.getFirst()
                                .getBlocoHorario()
                                .getHoraFim();

                // Procura o horário mais cedo do dia atual
                LocalTime inicioDiaAtual = aulasDiaAtual.stream()
                                .map(alocacao -> alocacao.getBlocoHorario().getHoraInicio())
                                .min(LocalTime::compareTo)
                                .orElseThrow(() -> new BusinessException("Nenhuma aula encontrada para o dia."));

                long minutosDescanso = calcularMinutosDescansoInterjornada(fimDiaAnterior, inicioDiaAtual);

                if (minutosDescanso < DESCANSO_MINIMO_INTERJORNADA.toMinutes()) {
                        throw new BusinessException(
                                        "O intervalo mínimo de interjornada de 12h não foi respeitado.");
                }
        }

        /**
         * Calcula o tempo de descanso entre jornadas.
         *
         * Exemplo:
         * Última aula termina às 18:00
         * Primeira aula do dia seguinte começa às 07:00
         *
         * Descanso:
         * (24:00 - 18:00) + 07:00
         * 6h + 7h = 13h
         */
        private long calcularMinutosDescansoInterjornada(
                        LocalTime fimDiaAnterior,
                        LocalTime inicioDiaAtual) {

                long minutosFimDiaAnterior = fimDiaAnterior.toSecondOfDay() / 60;

                long minutosInicioDiaAtual = inicioDiaAtual.toSecondOfDay() / 60;

                return (MINUTOS_POR_DIA - minutosFimDiaAnterior)
                                + minutosInicioDiaAtual;
        }
}