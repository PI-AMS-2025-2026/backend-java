package com.fatec.gini.domain.services.usecase.write;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarCopiarQuadroHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCursoAtivoUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CopiarQuadroHorarioUseCase {

    private final QuadroHorarioRepository gradeHorariaRepository;

    private final AlocacaoRepository alocacaoRepository;

    private final ValidarCopiarQuadroHorarioUseCase validarCopiaGradeHorariaUseCase;

    private final ValidarQuadroHorarioValidoUseCase validarGradeHorariaValidaUseCase;

    private final ValidarCursoAtivoUseCase validarCursoAtivoUseCase;

    @Transactional
    public QuadroHorario executar(
            Long idGradeOrigem,
            QuadroHorario dadosNovaGrade) {

        /*
         * Busca primeiro o quadro de origem.
         */
        QuadroHorario gradeAnterior =
                gradeHorariaRepository.findById(
                        idGradeOrigem)
                        .orElseThrow(() -> new BusinessException(
                                "Quadro horário de origem não encontrada."));

        /*
         * O curso da grade que será reaproveitada precisa
         * estar ativo.
         */
        validarCursoAtivoUseCase.validar(
                gradeAnterior.getCurso());

        /*
         * Busca as alocações da grade de origem uma única vez.
         */
        List<Alocacao> alocacoesAnteriores =
                alocacaoRepository.findByQuadroHorarioId(
                        idGradeOrigem);

        /*
         * Executa as validações existentes para garantir
         * que a grade pode ser copiada.
         */
        validarCopiaGradeHorariaUseCase
                .validarRegrasParaCopiaDeGrade(
                        alocacoesAnteriores);

        /*
         * Cria uma nova entidade.
         *
         * A grade de origem nunca é alterada.
         */
        QuadroHorario novoQuadro =
                new QuadroHorario();

        /*
         * A nova versão será baseada na versão informada
         * para a nova grade.
         */
        novoQuadro.setVersao(
                dadosNovaGrade.getVersao() != null
                        ? dadosNovaGrade.getVersao() + 1
                        : gradeAnterior.getVersao() + 1);

        LocalDateTime agora =
                LocalDateTime.now();

        novoQuadro.setDataCriacao(agora);

        /*
         * Mantém o comportamento existente da cópia:
         * a nova grade é criada como ATIVA.
         */
        novoQuadro.setStatus(
                Status.ATIVO);

        /*
         * O curso é mantido da grade de origem.
         */
        novoQuadro.setCurso(
                gradeAnterior.getCurso());

        /*
         * O período é o informado para a nova grade.
         */
        novoQuadro.setPeriodoAtividadeQuadro(
                dadosNovaGrade.getPeriodoAtividadeQuadro());

        novoQuadro.setCreatedAt(agora);
        novoQuadro.setUpdatedAt(agora);

        /*
         * Verifica se já existe outro quadro ativo
         * para o mesmo curso e período.
         */
        validarGradeHorariaValidaUseCase
                .validarQuadroAtivoDuplicado(
                        novoQuadro);

        /*
         * Persiste somente a nova grade.
         */
        novoQuadro =
                gradeHorariaRepository.save(
                        novoQuadro);

        /*
         * Cria novas alocações.
         *
         * As entidades são novas e apontam para o novo quadro,
         * portanto alterações futuras não modificam as
         * alocações da grade de origem.
         */
        List<Alocacao> novasAlocacoes =
                new ArrayList<>();

        for (Alocacao antiga :
                alocacoesAnteriores) {

            Alocacao nova =
                    new Alocacao();

            nova.setQuadroHorario(
                    novoQuadro);

            nova.setTurma(
                    antiga.getTurma());

            nova.setDiaSemana(
                    antiga.getDiaSemana());

            nova.setBlocoHorario(
                    antiga.getBlocoHorario());

            nova.setDisciplina(
                    antiga.getDisciplina());

            nova.setProfessor(
                    antiga.getProfessor());

            nova.setSala(
                    antiga.getSala());

            novasAlocacoes.add(
                    nova);
        }

        /*
         * Salva todas as novas alocações em lote.
         */
        alocacaoRepository.saveAll(
                novasAlocacoes);

        return novoQuadro;
    }
}