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
        //antes estava isAfter por isso o dataFim estava menor que dataInicio
        if (dataFim.isBefore(dataInicio)) {
            throw new ParameterException("Data de fim deve ser posterior à data de início");
        }

        //regra nova para que se a data de inicio for menor que 2008 vai exibir a mensagem de erro
        if(dataInicio.isBefore(LocalDate.of(2008, 1, 1))) {
            throw new ParameterException("Data de início deve ser a partir de 01/01/2008");
        }

    }

    public void executarAno(Integer ano, LocalDate dataInicio) {
    
       if (ano < 2008) {
        throw new ParameterException("O ano informado deve ser a partir de 2008");
    }
    }


   public void periodoValido(LocalDate dataInicio, LocalDate dataFim, Integer periodo) {

    // Início deve ser dia 1 e fim deve ser o último dia do mês
    boolean inicioPrimeiroDia = dataInicio.getDayOfMonth() == 1;
    boolean fimUltimoDia = dataFim.equals(dataFim.with(TemporalAdjusters.lastDayOfMonth()));

    if (!inicioPrimeiroDia || !fimUltimoDia) {
        throw new ParameterException(
            "O intervalo entre as datas não corresponde a meses completos."
        );
    }

    // Soma 1 dia ao fim (último dia do mês -> primeiro dia do mês seguinte)
    // para o ChronoUnit contar os meses cheios corretamente
    long totalMeses = ChronoUnit.MONTHS.between(dataInicio, dataFim.plusDays(1));

    // Semestral: deve começar em janeiro ou julho
    if (totalMeses == 6) {
        if (dataInicio.getMonthValue() == 1 || dataInicio.getMonthValue() == 7) {
            return;
        }
        throw new ParameterException(
            "O intervalo entre as datas deve corresponder a um período semestral."
        );
    }

    // Anual: deve começar em janeiro
    if (totalMeses == 12 && dataInicio.getMonthValue() == 1) {
        return;
    }

    throw new ParameterException(
        "O intervalo entre as datas deve corresponder a um período semestral ou anual."
    );
}
}
