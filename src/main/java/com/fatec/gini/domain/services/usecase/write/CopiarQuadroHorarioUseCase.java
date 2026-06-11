package com.fatec.gini.domain.services.usecase.write;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarCopiarQuadroHorarioUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CopiarQuadroHorarioUseCase {

    private final  QuadroHorarioRepository gradeHorariaRepository;

    private final  AlocacaoRepository alocacaoRepository;

    private final  ValidarCopiarQuadroHorarioUseCase validarCopiaGradeHorariaUseCase;

    // NOVA VALIDAÇÃO
    private final  ValidarQuadroHorarioValidoUseCase validarGradeHorariaValidaUseCase;

    @Transactional
    public QuadroHorario executar(Long idGradeOrigem, QuadroHorario dadosNovaGrade) {

        // busca alocações uma vez só
        List<Alocacao> alocacoesAnteriores =
                alocacaoRepository.findByQuadroHorarioId(idGradeOrigem);

        // valida antes da regra principal
        validarCopiaGradeHorariaUseCase
                .validarRegrasParaCopiaDeGrade(alocacoesAnteriores);

        // busca grade origem
        QuadroHorario gradeAnterior = gradeHorariaRepository.findById(idGradeOrigem)
                .orElseThrow(() ->
                        new BusinessException("Quadro horário de origem não encontrada."));

        // cria nova grade
        QuadroHorario novoQuadro = new QuadroHorario();

        novoQuadro.setVersao(
                dadosNovaGrade.getVersao() != null
                        ? dadosNovaGrade.getVersao() + 1
                        : 1);

        novoQuadro.setDataCriacao(LocalDateTime.now());

        novoQuadro.setStatus(Status.ATIVO);

        novoQuadro.setCurso(gradeAnterior.getCurso());

        novoQuadro.setPeriodoAtividadeQuadro(dadosNovaGrade.getPeriodoAtividadeQuadro());

        // NOVA VALIDAÇÃO DA ISSUE #56
        validarGradeHorariaValidaUseCase
                .validarQuadroAtivoDuplicado(novoQuadro);

        // salva nova grade
        novoQuadro = gradeHorariaRepository.save(novoQuadro);

        // cria novas alocações
        List<Alocacao> novasAlocacoes = new ArrayList<>();

        for (Alocacao antiga : alocacoesAnteriores) {

            Alocacao nova = new Alocacao();

            nova.setQuadroHorario(novoQuadro);
            nova.setTurma(antiga.getTurma());
            nova.setDiaSemana(antiga.getDiaSemana());
            nova.setBlocoHorario(antiga.getBlocoHorario());
            nova.setDisciplina(antiga.getDisciplina());
            nova.setProfessor(antiga.getProfessor());
            nova.setSala(antiga.getSala());

            novasAlocacoes.add(nova);
        }

        alocacaoRepository.saveAll(novasAlocacoes);

        return novoQuadro;
    }
}