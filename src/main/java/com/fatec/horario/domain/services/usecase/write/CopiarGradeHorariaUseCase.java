package com.fatec.horario.domain.services.usecase.write;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.Status;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.horario.web.exception.BusinessException;

@Service
public class CopiarGradeHorariaUseCase {

    @Autowired
    private GradeHorariaRepository gradeHorariaRepository;

    @Autowired
    private AlocacaoRepository alocacaoRepository;

    @Autowired
    private ValidarCopiarGradeHorariaUseCase validarCopiaGradeHorariaUseCase;

    @Transactional
    public GradeHoraria executar(Long idGradeOrigem, GradeHoraria dadosNovaGrade) {

        // busca alocações uma vez só
        List<Alocacao> alocacoesAnteriores = alocacaoRepository.findByGradeHorariaId(idGradeOrigem);

        // valida antes da regra principal
        validarCopiaGradeHorariaUseCase
                .validarRegrasParaCopiaDeGrade(alocacoesAnteriores);

        // busca grade origem
        GradeHoraria gradeAnterior = gradeHorariaRepository.findById(idGradeOrigem)
                .orElseThrow(() -> new BusinessException("Grade horária de origem não encontrada."));

        // cria nova grade
        GradeHoraria novaGrade = new GradeHoraria();
        novaGrade.setVersao(
                dadosNovaGrade.getVersao() != null
                        ? dadosNovaGrade.getVersao()
                        : 1);

        novaGrade.setDataCriacao(LocalDateTime.now());
        novaGrade.setStatus(Status.ATIVO);
        novaGrade.setCurso(gradeAnterior.getCurso());
        novaGrade.setPeriodoLetivo(dadosNovaGrade.getPeriodoLetivo());

        novaGrade = gradeHorariaRepository.save(novaGrade);

        // cria novas alocações
        List<Alocacao> novasAlocacoes = new ArrayList<>();

        for (Alocacao antiga : alocacoesAnteriores) {

            Alocacao nova = new Alocacao();

            nova.setGradeHoraria(novaGrade);
            nova.setTurma(antiga.getTurma());
            nova.setDiaSemana(antiga.getDiaSemana());
            nova.setHorario(antiga.getHorario());
            nova.setDisciplina(antiga.getDisciplina());
            nova.setUsuario(antiga.getUsuario());
            nova.setSala(antiga.getSala());

            novasAlocacoes.add(nova);
        }

        alocacaoRepository.saveAll(novasAlocacoes);

        return novaGrade;
    }
}