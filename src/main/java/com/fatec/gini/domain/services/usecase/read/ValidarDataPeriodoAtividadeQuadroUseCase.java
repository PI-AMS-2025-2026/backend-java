package com.fatec.gini.domain.services.usecase.read;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Service;

import com.fatec.gini.web.exception.ParameterException;

/**
 * Valida se data de fim é posterior a data de inicio
 */
@Service
public class ValidarDataPeriodoAtividadeQuadroUseCase {

    public void executar(LocalDate dataInicio, LocalDate dataFim) {
        // antes estava isAfter por isso o dataFim estava menor que dataInicio
        if (dataFim.isBefore(dataInicio)) {
            throw new ParameterException("Data de fim deve ser posterior à data de início");
        }

        // regra nova para que se a data de inicio for menor que 2008 vai exibir a
        // mensagem de erro
        if (dataInicio.isBefore(LocalDate.of(2008, 1, 1))) {
            throw new ParameterException("Data de início deve ser a partir de 01/01/2008");
        }

    }

    public void executarAno(Integer ano, LocalDate dataInicio) {

        if (ano < 2008) {
            throw new ParameterException("O ano informado deve ser a partir de 2008");
        }
    }

    public void periodoValido(LocalDate dataInicio, LocalDate dataFim, Integer periodo) {

        if (periodo == null || (periodo != 6 && periodo != 12)) {
            throw new ParameterException("O período informado deve ser semestral (6) ou anual (12).");
        }

        boolean inicioPrimeiroDia = dataInicio.getDayOfMonth() == 1;
        boolean fimUltimoDia = dataFim.equals(dataFim.with(TemporalAdjusters.lastDayOfMonth()));

        if (!inicioPrimeiroDia || !fimUltimoDia) {
            throw new ParameterException("O intervalo entre as datas não corresponde a meses completos.");
        }

        long totalMeses = ChronoUnit.MONTHS.between(dataInicio, dataFim.plusDays(1));

        if (totalMeses != periodo) {
            throw new ParameterException("O intervalo entre as datas não corresponde ao período selecionado.");
        }

        int mesInicio = dataInicio.getMonthValue();

        // Semestral: começa em janeiro ou julho
        if (periodo == 6 && mesInicio != 1 && mesInicio != 7) {
            throw new ParameterException("O período semestral deve começar em janeiro ou julho.");
        }

        // Anual: começa em janeiro
        if (periodo == 12 && mesInicio != 1) {
            throw new ParameterException("O período anual deve começar em janeiro.");
        }
    }
}
