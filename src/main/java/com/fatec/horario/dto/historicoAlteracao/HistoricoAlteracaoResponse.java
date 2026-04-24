package com.fatec.horario.dto.historicoAlteracao;

import java.time.LocalDate;

import com.fatec.horario.dto.alocacao.AlocacaoResponse;
import com.fatec.horario.dto.usuario.UsuarioResponse;

public record HistoricoAlteracaoResponse(
        Long id,
        LocalDate dataAlteracao,
        String justificativa,
        String campoAlterado,
        String valorAntigo,
        String valorNovo,
        AlocacaoResponse alocacao,
        UsuarioResponse usuario) {

}
