package com.fatec.gini.domain.services.usecase.read;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

import com.fatec.gini.domain.models.TipoPeridoAtividadeQuadro;
import com.fatec.gini.web.exception.ParameterException;

/**
 * Valida se data de fim é posterior a data de inicio
 */
@Service
public class ValidarDataPeriodoAtividadeQuadroUseCase {

    public void executar(LocalDate dataInicio, LocalDate dataFim) {
        // antes estava isAfter por isso o dataFim estava menor que dataInicio
        if (!dataFim.isAfter(dataInicio)) {
            throw new ParameterException("Data de fim deve ser posterior à data de início");
        }

        // regra nova para que se a data de inicio for menor que 2008 vai exibir a
        // mensagem de erro
        if (dataInicio.isBefore(LocalDate.of(2008, 1, 1))) {
            throw new ParameterException("Data de início deve ser a partir de 01/01/2008");
        }

    }

    public void executarAno(Integer ano, LocalDate dataInicio, LocalDate dataFim) {
        if (ano == null || ano < 2008) {
            throw new ParameterException("O ano informado deve ser a partir de 2008");
        }

        if (dataInicio.getYear() != ano || dataFim.getYear() != ano) {
            throw new ParameterException("As datas de vigência devem pertencer ao ano informado.");
        }
    }

    public void periodoValido(LocalDate dataInicio, LocalDate dataFim, Integer periodo,
            TipoPeridoAtividadeQuadro tipoPeriodo) {
        if (tipoPeriodo == null || periodo == null) {
            throw new ParameterException("O tipo e o número do período são obrigatórios.");
        }

        if (tipoPeriodo == TipoPeridoAtividadeQuadro.SEMENSTRAL) {
            if (periodo < 1 || periodo > 2) {
                throw new ParameterException("O período semestral deve ser 1 ou 2.");
            }
            int mesInicio = dataInicio.getMonthValue();
            int mesFim = dataFim.getMonthValue();
            if ((periodo == 1 && (mesInicio > 6 || mesFim > 6))
                    || (periodo == 2 && (mesInicio < 7 || mesFim < 7))) {
                throw new ParameterException("As datas não correspondem ao semestre informado.");
            }
        } else if (tipoPeriodo == TipoPeridoAtividadeQuadro.ANUAL && periodo != 1) {
            throw new ParameterException("O período anual deve ser 1.");
        }
    }
}
