package com.fatec.horario.domain.services.usecase.write;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.HistoricoAlteracao;
import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.domain.entities.Turma;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.infrastructure.repositories.HistoricoAlteracaoRepository;

@Service
public class AlteracaoAlocacaoUseCase {

    @Autowired
    private HistoricoAlteracaoRepository historicoAlteracaoRepository;

    public void executarCasoUso(
            Alocacao alocacaoAtual,
            Turma novaTurma,
            Disciplina novaDisciplina,
            Sala novaSala,
            Usuario novoUsuario,
            Usuario usuarioAlteracao,
            DiaSemana novoDiaSemana,
            Horario novoHorario,
            GradeHoraria novaGradeHoraria,
            String justificativa) {

        registrarSeAlterou("turma", alocacaoAtual.getTurma().getId(), novaTurma.getId(), alocacaoAtual,
                usuarioAlteracao, justificativa);
        registrarSeAlterou("disciplina", alocacaoAtual.getDisciplina().getId(), novaDisciplina.getId(), alocacaoAtual,
                usuarioAlteracao, justificativa);
        registrarSeAlterou("sala", alocacaoAtual.getSala().getId(), novaSala.getId(), alocacaoAtual,
                usuarioAlteracao, justificativa);
        registrarSeAlterou("usuario", alocacaoAtual.getUsuario().getId(), novoUsuario.getId(), alocacaoAtual,
                usuarioAlteracao, justificativa);
        registrarSeAlterou("diaSemana", alocacaoAtual.getDiaSemana().getId(), novoDiaSemana.getId(), alocacaoAtual,
                usuarioAlteracao, justificativa);
        registrarSeAlterou("horario", alocacaoAtual.getHorario().getId(), novoHorario.getId(), alocacaoAtual,
                usuarioAlteracao, justificativa);
        registrarSeAlterou("gradeHoraria", alocacaoAtual.getGradeHoraria().getId(), novaGradeHoraria.getId(),
                alocacaoAtual, usuarioAlteracao, justificativa);
    }

    private void registrarSeAlterou(
            String campoAlterado,
            Long valorAntigo,
            Long valorNovo,
            Alocacao alocacao,
            Usuario usuario,
            String justificativa) {

        if (Objects.equals(valorAntigo, valorNovo)) {
            return;
        }

        HistoricoAlteracao historicoAlteracao = new HistoricoAlteracao();
        historicoAlteracao.setDataAlteracao(LocalDate.now());
        historicoAlteracao.setJustificativa(justificativa);
        historicoAlteracao.setCampoAlterado(campoAlterado);
        historicoAlteracao.setValorAntigo(valorAntigo != null ? valorAntigo.toString() : null);
        historicoAlteracao.setValorNovo(valorNovo != null ? valorNovo.toString() : null);
        historicoAlteracao.setAlocacao(alocacao);
        historicoAlteracao.setUsuario(usuario);

        historicoAlteracaoRepository.save(historicoAlteracao);
    }
}
