package com.fatec.gini.domain.services.usecase.read;

import java.time.LocalDate;

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

    }

    public void executarAno(Integer ano, LocalDate dataInicio) {
       if(dataInicio.getYear() != ano){
        throw new ParameterException("Ano informado não corresponde ao ano da data de início");
       }
    }
}
