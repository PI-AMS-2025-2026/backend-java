package com.fatec.gini.domain.services.usecase.write;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.GradeHoraria;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarCopiarGradeHorariaUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CopiarGradeHorariaUseCase {

    private final  GradeHorariaRepository gradeHorariaRepository;

    private final  AlocacaoRepository alocacaoRepository;

    private final  ValidarCopiarGradeHorariaUseCase validarCopiaGradeHorariaUseCase;

    // NOVA VALIDAÇÃO
    private final  ValidarGradeHorariaValidaUseCase validarGradeHorariaValidaUseCase;

    @Transactional
    public GradeHoraria executar(Long idGradeOrigem, GradeHoraria dadosNovaGrade) {

        // busca alocações uma vez só
        List<Alocacao> alocacoesAnteriores =
                alocacaoRepository.findByGradeHorariaId(idGradeOrigem);

        // valida antes da regra principal
        validarCopiaGradeHorariaUseCase
                .validarRegrasParaCopiaDeGrade(alocacoesAnteriores);

        // busca grade origem
        GradeHoraria gradeAnterior = gradeHorariaRepository.findById(idGradeOrigem)
                .orElseThrow(() ->
                        new BusinessException("Grade horária de origem não encontrada."));

        // cria nova grade
        GradeHoraria novaGrade = new GradeHoraria();

        novaGrade.setVersao(
                dadosNovaGrade.getVersao() != null
                        ? dadosNovaGrade.getVersao() + 1
                        : 1);

        novaGrade.setDataCriacao(LocalDateTime.now());

        novaGrade.setStatus(Status.ATIVO);

        novaGrade.setCurso(gradeAnterior.getCurso());

        novaGrade.setPeriodoLetivo(dadosNovaGrade.getPeriodoLetivo());

        // NOVA VALIDAÇÃO DA ISSUE #56
        validarGradeHorariaValidaUseCase
                .validarGradeAtivaDuplicada(novaGrade);

        // salva nova grade
        novaGrade = gradeHorariaRepository.save(novaGrade);

        // cria novas alocações
        List<Alocacao> novasAlocacoes = new ArrayList<>();

        for (Alocacao antiga : alocacoesAnteriores) {

            Alocacao nova = new Alocacao();

            nova.setGradeHoraria(novaGrade);
            nova.setTurma(antiga.getTurma());
            nova.setDiaSemana(antiga.getDiaSemana());
            nova.setBlocoHorario(antiga.getBlocoHorario());
            nova.setDisciplina(antiga.getDisciplina());
            nova.setProfessor(antiga.getProfessor());
            nova.setSala(antiga.getSala());

            novasAlocacoes.add(nova);
        }

        alocacaoRepository.saveAll(novasAlocacoes);

        return novaGrade;
    }
}