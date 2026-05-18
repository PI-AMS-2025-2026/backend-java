package com.fatec.horario.domain.services.usecase.read;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.web.exception.BusinessException;

@Service
public class ValidarCargaHorariaMaximaProfessorUseCase {

        @Autowired
        private AlocacaoRepository alocacaoRepository;

        //TODO: verificar o real valor para limite de horas diárias, e se tem alguma regra específica para aulas noturnas ou sábados
        private static final long LIMITE_HORAS_DIARIAS = 8; 

        @Transactional(readOnly = true)
        public void validar(Long professorId, Long diaSemanaId) {

                validarCargaHorariaDiaria(professorId, diaSemanaId);
                validarDescansoMinimo12Horas(professorId, diaSemanaId);
        }

        // carga horaria maxima por dia
        private void validarCargaHorariaDiaria(Long professorId, Long diaSemanaId) {
                // verificar todas as aulas que o professor deu no dia
                List<Alocacao> alocacoes = alocacaoRepository.findByProfessorAndDiaSemana(professorId, diaSemanaId);

                long totalMinutos = alocacoes.stream()
                                .mapToLong(a -> Duration.between(
                                                a.getHorario().getHoraInicio(),
                                                // calcula a duração de cada aula
                                                a.getHorario().getHoraFim()).toMinutes())
                                .sum();
                // soma todas as durações

                long totalHoras = totalMinutos / 60;

                if (totalHoras > LIMITE_HORAS_DIARIAS) {
                        // se passar daquele horario definido la em cima retorna a mensagem abaixo
                        throw new BusinessException(
                                        "A carga horária máxima diária do professor foi excedida.");
                }
        }

        // regra das 12h de descanso interjornada
        private void validarDescansoMinimo12Horas(Long professorId, Long diaSemanaId) {
                Long diaAnterior = calcularDiaAnterior(diaSemanaId);

                List<Alocacao> aulasDiaAnterior = alocacaoRepository.findUltimaAulaDoDia(professorId, diaAnterior);
                List<Alocacao> aulasDiaAtual = alocacaoRepository.findByProfessorAndDiaSemana(professorId, diaSemanaId);

                // Sem aulas em qualquer um dos dias = sem restrição de descanso
                if (aulasDiaAnterior.isEmpty() || aulasDiaAtual.isEmpty()) {
                        return;
                }

                Alocacao ultimaAulaDiaAnterior = aulasDiaAnterior.get(0);
                LocalTime fimDiaAnterior = ultimaAulaDiaAnterior.getHorario().getHoraFim();

                // Obtém a primeira aula (mais cedo) do dia atual
                LocalTime inicioDiaAtual = aulasDiaAtual.stream()
                                .map(a -> a.getHorario().getHoraInicio())
                                .min(LocalTime::compareTo)
                                .orElseThrow(() -> new BusinessException("Nenhuma aula encontrada para o dia."));

                long minutosDescanso = calcularMinutosDescansoInterjornada(fimDiaAnterior, inicioDiaAtual);

                if (minutosDescanso < (12 * 60)) {
                        throw new BusinessException(
                                        "O intervalo mínimo de interjornada de 12h não foi respeitado.");
                }
        }

        private Long calcularDiaAnterior(Long diaSemanaId) {
                // Calcula o dia anterior da semana com wrapping (domingo=7 para sábado=6)
                return (diaSemanaId == 1) ? 7 : diaSemanaId - 1;
        }

        private long calcularMinutosDescansoInterjornada(LocalTime fimDiaAnterior, LocalTime inicioDiaAtual) {
                // Converte para minutos desde meia-noite para precisão na matemática inteira
                long minutosFimOntem = fimDiaAnterior.toSecondOfDay() / 60;
                long minutosInicioHoje = inicioDiaAtual.toSecondOfDay() / 60;

                // Fórmula: (minutos restantes do dia anterior) + (minutos do dia novo até primeira aula)
                // Exemplo: aula termina 18:00 (1080 min), começa 07:00 (420 min)
                // Descanso: (1440 - 1080) + 420 = 360 + 420 = 780 min = 13 horas ✓
                return (1440 - minutosFimOntem) + minutosInicioHoje;
        }
}