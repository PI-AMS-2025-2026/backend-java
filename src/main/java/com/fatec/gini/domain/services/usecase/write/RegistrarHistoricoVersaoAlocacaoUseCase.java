package com.fatec.gini.domain.services.usecase.write;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.HistoricoVersaoAlocacao;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.infrastructure.repositories.HistoricoVersaoAlocacaoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Use case responsável por registrar o histórico de versão das alocações.
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
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RegistrarHistoricoVersaoAlocacaoUseCase {

    private final HistoricoVersaoAlocacaoRepository historicoVersaoAlocacaoRepository;

    /*
     * O usuarioAlteracao é o usuário que realizou a operação (criação ou
     * atualização) na alocação. Ele é necessário para registrar quem fez a
     * alteração no histórico de versão, garantindo rastreabilidade e responsabilidade pelas
     * mudanças realizadas.
     */
    @Transactional
    public void registrarCriacao(Alocacao alocacaoSalva, Usuario usuarioAlteracao) {
        registrar(
                "alocacao",
                null,
                alocacaoSalva.getId().toString(), // Registra apenas o ID da alocação no histórico.
                alocacaoSalva,
                usuarioAlteracao);
    }

    @Transactional
    public void registrarAtualizacao(Alocacao alocacaoSalva, Alocacao alocacaoAntiga, Usuario usuarioAlteracao) {

        // Turma
        registrar("turma", alocacaoAntiga.getTurma().getId().toString(), alocacaoSalva.getTurma().getId().toString(),
                alocacaoSalva,
                usuarioAlteracao);
        // Disciplina
        registrar("disciplina", alocacaoAntiga.getDisciplina().getId().toString(),
                alocacaoSalva.getDisciplina().getId().toString(),
                alocacaoSalva,
                usuarioAlteracao);

        // Sala
        registrar("sala", alocacaoAntiga.getSala().getId().toString(), alocacaoSalva.getSala().getId().toString(),
                alocacaoSalva,
                usuarioAlteracao);

        // Professor
        registrar("professor", alocacaoAntiga.getProfessor().getId().toString(),
                alocacaoSalva.getProfessor().getId().toString(),
                alocacaoSalva,
                usuarioAlteracao);

        // Dia da semana
        registrar("diaSemana", alocacaoAntiga.getDiaSemana().toString(),
                alocacaoSalva.getDiaSemana().toString(),
                alocacaoSalva,
                usuarioAlteracao);

        // Bloco de horário
        registrar("blocoHorario", alocacaoAntiga.getBlocoHorario().getId().toString(),
                alocacaoSalva.getBlocoHorario().getId().toString(),
                alocacaoSalva,
                usuarioAlteracao);
        // Quadro horário
        registrar("quadroHorario", alocacaoAntiga.getQuadroHorario().getId().toString(),
                alocacaoSalva.getQuadroHorario().getId().toString(),
                alocacaoSalva, usuarioAlteracao);
    }

    private void registrar(
            String campoAlterado,
            String valorAntigo,
            String valorNovo,
            Alocacao alocacao,
            Usuario usuario) {

        if (Objects.equals(valorAntigo, valorNovo)) {
            return;
        }

        HistoricoVersaoAlocacao historicoVersaoAlocacao = new HistoricoVersaoAlocacao();
        historicoVersaoAlocacao.setDataAlteracao(LocalDate.now());
        historicoVersaoAlocacao.setCampoAlterado(campoAlterado);
        historicoVersaoAlocacao.setValorAntigo(valorAntigo != null ? valorAntigo.toString() : null);
        historicoVersaoAlocacao.setValorNovo(valorNovo != null ? valorNovo.toString() : null);
        historicoVersaoAlocacao.setAlocacao(alocacao);
        historicoVersaoAlocacao.setUsuario(usuario);

        historicoVersaoAlocacaoRepository.save(historicoVersaoAlocacao);
    }

}
