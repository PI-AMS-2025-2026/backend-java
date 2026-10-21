package com.fatec.gini.dto.historicoVersaoAlocacao;

import java.time.LocalDate;

import com.fatec.gini.dto.alocacao.AlocacaoResponse;
import com.fatec.gini.dto.usuario.UsuarioResponse;

public record HistoricoVersaoAlocacaoResponse(
        Long id,
        LocalDate dataAlteracao,
        String justificativa,
        String campoAlterado,
        String valorAntigo,
        String valorNovo,
        AlocacaoResponse alocacao,
        UsuarioResponse usuario) {

}
