package com.fatec.gini.dto.sugestaoAutomatica;

import com.fatec.gini.dto.alocacao.AlocacaoRequest;

import java.util.List;

public record SugestaoResponse(

        boolean sucesso,

        String mensagem,

        String motivo,

        List<AlocacaoRequest> sugestoes

) {
}