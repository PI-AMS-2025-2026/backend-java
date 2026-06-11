package com.fatec.gini.domain.services.usecase.read;

import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidarCargaHorariaDisciplinaUseCase {

    private final  AlocacaoRepository alocacaoRepository;

    private final  DisciplinaRepository disciplinaRepository;

    public void executar(Alocacao entity) {
        var disciplina = disciplinaRepository.findById(entity.getDisciplina().getId())
                .orElseThrow(() -> new BusinessException("Disciplina não encontrada."));

        // Busca o somatório filtrando por Disciplina E por Grade Horária
        int cargaHorariaAtual = alocacaoRepository.somarDuracaoPorDisciplinaEGrade(
                disciplina.getId(), 
                entity.getGradeHoraria().getId()
        );
        int novaDuracao = entity.getBlocoHorario().getDuracao();

        if (cargaHorariaAtual + novaDuracao > disciplina.getCargaHoraria()) {
            throw new BusinessException("A carga horária total da disciplina já foi preenchida na grade horária.");
        }
    }
}