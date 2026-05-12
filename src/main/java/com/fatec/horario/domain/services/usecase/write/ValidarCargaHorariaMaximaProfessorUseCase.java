package com.fatec.horario.domain.services.usecase.write;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.web.exception.BusinessException;

@Service
public class ValidarCargaHorariaMaximaProfessorUseCase {

      private final AlocacaoRepository alocacaoRepository;


    public ValidarCargaHorariaMaximaProfessorUseCase(AlocacaoRepository alocacaoRepository) {
        this.alocacaoRepository = alocacaoRepository;
    }

    private static final long LIMITE_HORAS_DIARIAS = 8; //conferir se é isso mesmo de limite

    @Transactional(readOnly = true)
    public void validar(Long professorId, Long diaSemanaId) {

        validarCargaHorariaDiaria(professorId, diaSemanaId);

        validarDescansoMinimo12Horas(professorId, diaSemanaId);
    }

    // carga horaria maxima por dia
    private void validarCargaHorariaDiaria(Long professorId, Long diaSemanaId) {

        List<Alocacao> alocacoes =
                alocacaoRepository.findByProfessorAndDiaSemana(professorId, diaSemanaId); // ve todas as aulas que o professor deu no dia 

        long totalMinutos = alocacoes.stream()
                .mapToLong(a -> Duration.between(
                        a.getHorario().getHoraInicio(), //calcula a duração de cada aula
                        a.getHorario().getHoraFim()
                ).toMinutes())
                .sum(); //soma todas as durações

        long totalHoras = totalMinutos / 60;

        if (totalHoras > LIMITE_HORAS_DIARIAS) { //se passar daquele horario definido la emcima retorna a mensagem abaixo
            throw new BusinessException(
                    "A carga horária máxima diária do professor foi excedida."
            );
        }
    }

    // regra das 12h de descanso
    private void validarDescansoMinimo12Horas(Long professorId, Long diaSemanaId) {

        Long diaAnterior = (diaSemanaId == 1) ? 7 : diaSemanaId - 1; //descobre o dia anterior (verifica se foi dia util)

        List<Alocacao> aulasDiaAnterior =
                alocacaoRepository.findUltimaAulaDoDia(professorId, diaAnterior); //busca as aulas do dia anterior

        List<Alocacao> aulasDiaAtual =
                alocacaoRepository.findByProfessorAndDiaSemana(professorId, diaSemanaId); //busca as aulas do dia atual

        if (aulasDiaAnterior.isEmpty() || aulasDiaAtual.isEmpty()) {
            return;
        }

        Alocacao ultimaAulaDiaAnterior = aulasDiaAnterior.get(0);
        
        // pega a aula que começa mais cedo no dia atual
        LocalTime inicioDiaAtual = aulasDiaAtual.stream()
                .map(a -> a.getHorario().getHoraInicio())
                .min(LocalTime::compareTo).get();

        LocalTime fimDiaAnterior = ultimaAulaDiaAnterior.getHorario().getHoraFim(); // pega a ultima aula do dia

        // usei minutos para ter mais precisão no calculo (com horas podia dar horas negativas)

        // cálculo considerando que a última aula foi ontem e a primeira é hoje
        long minutosFimOntem = fimDiaAnterior.toSecondOfDay() / 60;
        long minutosInicioHoje = inicioDiaAtual.toSecondOfDay() / 60;
        
        // (Minutos que faltavam para acabar o dia anterior) + (minutos que já passaram hoje)
        long minutosDescanso = (1440 - minutosFimOntem) + minutosInicioHoje;

        if (minutosDescanso < (12 * 60)) { //se o descanso for menor que 12h mostra a mensagem de erro:
            throw new BusinessException(
                    "O intervalo mínimo de interjornada de 12h não foi respeitado."
            );
        }
    }
}