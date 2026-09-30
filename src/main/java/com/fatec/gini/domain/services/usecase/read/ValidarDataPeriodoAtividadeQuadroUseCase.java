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

     long totalMeses = ChronoUnit.MONTHS.between(dataInicio, dataFim);

    // Garante que as datas representam meses completos
    if (!dataInicio.plusMonths(totalMeses).equals(dataFim)) {
        throw new ParameterException(
            "O intervalo entre as datas não corresponde a meses completos."
        );
    }

    // Semestral
    if (totalMeses == 6) {
        if (dataInicio.getMonthValue() == 1 || dataInicio.getMonthValue() == 7) {
        return; // Primeiro semestre
        } else {
            throw new ParameterException(
                "O intervalo entre as datas deve corresponder a um período semestral."
            );
        
        }

    } 
    
    if (totalMeses == 12) {
     if(dataInicio.getMonthValue() == 1) {
        return;
     } 
     
    }

    throw new ParameterException(
        "O intervalo entre as datas deve corresponder a um período semestral ou anual."
    );
}


}
