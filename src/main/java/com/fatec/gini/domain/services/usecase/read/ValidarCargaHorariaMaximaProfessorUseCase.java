package com.fatec.gini.domain.services.usecase.read;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;
import com.fatec.gini.web.exception.ParameterException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarCargaHorariaMaximaProfessorUseCase {

        private final AlocacaoRepository alocacaoRepository;

        // Limite máximo de horas que um professor pode lecionar em um único dia
        private static final Duration LIMITE_CARGA_HORARIA_DIARIA = Duration.ofHours(8);

        // Intervalo mínimo obrigatório entre o fim de uma jornada e o início da próxima
        private static final Duration DESCANSO_MINIMO_INTERJORNADA = Duration.ofHours(11);

        // Quantidade de minutos existentes em um dia completo
        private static final long MINUTOS_POR_DIA = Duration.ofDays(1).toMinutes();

        @Transactional(readOnly = true, noRollbackFor = { BusinessException.class, ParameterException.class })
        public void validar(Long professorId, DiaSemana diaSemana) {
                validar(professorId, diaSemana, null);
        }

        @Transactional(readOnly = true, noRollbackFor = { BusinessException.class, ParameterException.class })
        public void validar(Long professorId, DiaSemana diaSemana, Alocacao alocacaoAtual) {

                // Valida se o professor ultrapassou a carga horária diária permitida
                validarCargaHorariaDiaria(professorId, diaSemana, alocacaoAtual);

                // Valida se existe pelo menos 11h de descanso entre jornadas
                validarDescansoMinimo11Horas(professorId, diaSemana, alocacaoAtual);
        }

        /**
         * Regra de negócio:
         * Um professor não pode ultrapassar 8 horas de aula em um único dia.
         */
        private void validarCargaHorariaDiaria(Long professorId, DiaSemana diaSemana, Alocacao alocacaoAtual) {

                List<Alocacao> alocacoes = new ArrayList<>(
                                alocacaoRepository.findByProfessorIdAndDiaSemana(professorId, diaSemana));

                if (alocacaoAtual != null) {
                        alocacoes.removeIf(alocacao -> Objects.equals(alocacao.getId(), alocacaoAtual.getId()));
                        alocacoes.add(alocacaoAtual);
                }

                // Soma a duração de todos os blocos de horário do dia
                long totalMinutos = alocacoes.stream()
                                .mapToLong(this::duracaoEmMinutos)
                                .sum();

                if (totalMinutos > LIMITE_CARGA_HORARIA_DIARIA.toMinutes()) {
                        throw new BusinessException(
                                        "A carga horária máxima diária do professor foi excedida.");
                }
        }

        private long duracaoEmMinutos(Alocacao alocacao) {
                if (alocacao == null || alocacao.getBlocoHorario() == null
                                || alocacao.getBlocoHorario().getHoraInicio() == null
                                || alocacao.getBlocoHorario().getHoraFim() == null
                                || !alocacao.getBlocoHorario().getHoraFim()
                                                .isAfter(alocacao.getBlocoHorario().getHoraInicio())) {
                        throw new ParameterException(
                                        "Existe uma alocação do professor com bloco de horário inválido.");
                }

                return Duration.between(
                                alocacao.getBlocoHorario().getHoraInicio(),
                                alocacao.getBlocoHorario().getHoraFim()).toMinutes();
        }

        /**
         * Regra de negócio:
         * Deve existir no mínimo 11 horas de descanso entre o término da última aula
         * de um dia e o início da primeira aula do dia seguinte.
         */
        private void validarDescansoMinimo11Horas(Long professorId, DiaSemana diaSemana, Alocacao alocacaoAtual) {

                List<Alocacao> alocacoesProfessor = new ArrayList<>(
                                alocacaoRepository.findByProfessorId(professorId));

                List<Alocacao> aulasDiaAtual = alocacoesProfessor.stream()
                                .filter(alocacao -> alocacao.getDiaSemana() == diaSemana)
                                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

                if (alocacaoAtual != null) {
                        alocacoesProfessor.removeIf(alocacao -> Objects.equals(alocacao.getId(), alocacaoAtual.getId()));
                        aulasDiaAtual.removeIf(alocacao -> Objects.equals(alocacao.getId(), alocacaoAtual.getId()));
                        if (alocacaoAtual.getDiaSemana() == diaSemana) {
                                aulasDiaAtual.add(alocacaoAtual);
                                alocacoesProfessor.add(alocacaoAtual);
                        }
                }

                if (aulasDiaAtual.isEmpty()) {
                        return;
                }

                DiaSemana diaAnterior = encontrarUltimoDiaComAulaAntesDe(alocacoesProfessor, diaSemana);
                if (diaAnterior == null) {
                        return;
                }

                List<Alocacao> aulasDiaAnterior = alocacoesProfessor.stream()
                                .filter(alocacao -> alocacao.getDiaSemana() == diaAnterior)
                                .sorted((a, b) -> b.getBlocoHorario().getHoraFim()
                                                .compareTo(a.getBlocoHorario().getHoraFim()))
                                .toList();

                Alocacao ultimaAulaDiaAnterior = aulasDiaAnterior.getFirst();
                if (ultimaAulaDiaAnterior == null || ultimaAulaDiaAnterior.getBlocoHorario() == null) {
                        throw new ParameterException(
                                        "Existe uma alocação anterior do professor com bloco de horário inválido.");
                }

                LocalTime fimDiaAnterior = ultimaAulaDiaAnterior.getBlocoHorario().getHoraFim();

                if (fimDiaAnterior == null) {
                        throw new ParameterException(
                                        "Existe uma alocação anterior do professor com horário inválido.");
                }

                LocalTime inicioDiaAtual = aulasDiaAtual.stream()
                                .map(alocacao -> alocacao.getBlocoHorario().getHoraInicio())
                                .min(LocalTime::compareTo)
                                .orElseThrow(() -> new BusinessException("Nenhuma aula encontrada para o dia."));

                long minutosDescanso = calcularMinutosDescansoInterjornada(
                                fimDiaAnterior, inicioDiaAtual, distanciaEmDias(diaAnterior, diaSemana));

                if (minutosDescanso < DESCANSO_MINIMO_INTERJORNADA.toMinutes()) {
                        throw new BusinessException(
                                        "O intervalo mínimo de interjornada de 11h não foi respeitado.");
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
                        LocalTime inicioDiaAtual,
                        int diasEntreJornadas) {

                long minutosFimDiaAnterior = fimDiaAnterior.toSecondOfDay() / 60;

                long minutosInicioDiaAtual = inicioDiaAtual.toSecondOfDay() / 60;

                return ((long) diasEntreJornadas * MINUTOS_POR_DIA - minutosFimDiaAnterior)
                                + minutosInicioDiaAtual;
        }

        private DiaSemana encontrarUltimoDiaComAulaAntesDe(List<Alocacao> alocacoes, DiaSemana diaAtual) {
                return alocacoes.stream()
                                .map(Alocacao::getDiaSemana)
                                .filter(dia -> dia != diaAtual)
                                .min((a, b) -> Integer.compare(distanciaEmDias(b, diaAtual), distanciaEmDias(a, diaAtual)))
                                .orElse(null);
        }

        private int distanciaEmDias(DiaSemana anterior, DiaSemana atual) {
                int distancia = atual.ordinal() - anterior.ordinal();
                return distancia > 0 ? distancia : distancia + DiaSemana.values().length;
        }
}
