package com.fatec.gini.domain.services.usecase.write;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.HistoricoAlteracao;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.infrastructure.repositories.HistoricoAlteracaoRepository;

/**
 * Use case responsável por registrar o histórico de alocações.
 *
 * <p>
 * Responsabilidades:
 * <ul>
 * <li>Registrar criação de alocações</li>
 * <li>Registrar alterações em alocações</li>
 * <li>Guardar estado anterior e novo estado quando necessário</li>
 * </ul>
 *
 * <p>
 * Notas:
 * <ul>
 * <li>Não valida regras de negócio</li>
 * <li>Executa apenas após aceitação da operação principal</li>
 * </ul>
 */
@Service
public class RegistrarHistoricoAlocacaoUseCase {

    @Autowired
    private HistoricoAlteracaoRepository historicoAlteracaoRepository;

    /*
     * O usuraioAlteracao é o usuário que realizou a operação (criação ou
     * atualização) na alocação. Ele é necessário para registrar quem fez a
     * alteração no histórico, garantindo rastreabilidade e responsabilidade pelas
     * mudanças realizadas.
     */

    @Transactional
    public void registrarCriacao(Alocacao alocacaoSalva, Usuario usuarioAlteracao) {
        registrar("Alocação_" + alocacaoSalva.getId(), null, alocacaoSalva.toString(), alocacaoSalva, usuarioAlteracao,
                "Criação de alocação");

    }

    @Transactional
    public void registrarAtualizacao(Alocacao alocacaoSalva, Alocacao alocacaoAntiga, Usuario usuarioAlteracao,
            String justificativa) {

        // Turma
        registrar("turma", alocacaoSalva.getTurma().getId().toString(), alocacaoAntiga.getTurma().getId().toString(),
                alocacaoSalva,
                usuarioAlteracao, justificativa);
        // Disciplina
        registrar("disciplina", alocacaoSalva.getDisciplina().getId().toString(),
                alocacaoAntiga.getDisciplina().getId().toString(),
                alocacaoSalva,
                usuarioAlteracao, justificativa);

        // Sala
        registrar("sala", alocacaoSalva.getSala().getId().toString(), alocacaoAntiga.getSala().getId().toString(),
                alocacaoSalva,
                usuarioAlteracao, justificativa);

        // Usuário (professor da alocação)
        registrar("usuario", alocacaoSalva.getUsuario().getId().toString(),
                alocacaoAntiga.getUsuario().getId().toString(),
                alocacaoSalva,
                usuarioAlteracao, justificativa);

        // Dia da semana
        registrar("diaSemana", alocacaoSalva.getDiaSemana().getId().toString(),
                alocacaoAntiga.getDiaSemana().getId().toString(),
                alocacaoSalva,
                usuarioAlteracao, justificativa);

        // Horário
        registrar("horario", alocacaoSalva.getHorario().getId().toString(),
                alocacaoAntiga.getHorario().getId().toString(),
                alocacaoSalva,
                usuarioAlteracao, justificativa);
        // Grade Horaria
        registrar("gradeHoraria", alocacaoSalva.getGradeHoraria().getId().toString(),
                alocacaoAntiga.getGradeHoraria().getId().toString(),
                alocacaoSalva, usuarioAlteracao, justificativa);
    }

    private void registrar(
            String campoAlterado,
            String valorAntigo,
            String valorNovo,
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
