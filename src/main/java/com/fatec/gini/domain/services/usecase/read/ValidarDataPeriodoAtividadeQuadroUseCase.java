package com.fatec.gini.domain.services.usecase.read;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
    // calcula o total de meses entre as duas datas
    long totalMeses = ChronoUnit.MONTHS.between(dataInicio, dataFim);
    
    // calcula quantos períodos (semestres) cabem nesse intervalo de meses
    long periodosCalculados = totalMeses / 6;
    
    if (periodo != periodosCalculados) {
        throw new ParameterException("Período informado não corresponde ao intervalo entre a data de início e fim.");
    }
}
}
