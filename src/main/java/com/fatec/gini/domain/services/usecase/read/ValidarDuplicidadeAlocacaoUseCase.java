package com.fatec.gini.domain.services.usecase.read;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

/**
 * Valida a duplicidade de alocações na criação de registros.
 * 
 * <p>
 * Responsável por impedir o cadastro de alocações duplicadas, verificando
 * se já existe outra alocação com a mesma combinação de dados de negócio.
 * </p>
 * 
 * <p>
 * <strong>Critério de duplicidade:</strong> A validação compara a assinatura
 * de negócio da alocação (não apenas o ID, que é normalmente gerado), levando
 * em conta os seguintes campos:
 * </p>
 * 
 * <ul>
 * <li>turma</li>
 * <li>disciplina</li>
 * <li>sala</li>
 * <li>usuário/professor</li>
 * <li>diaSemana</li>
 * <li>horário</li>
 * <li>gradeHoraria</li>
 * </ul>
 * 
 * <p>
 * Se todos os campos relevantes forem idênticos a outra alocação existente,
 * a operação é considerada duplicada e deve ser rejeitada.
 * </p>
 */
@Service
public class ValidarDuplicidadeAlocacaoUseCase {

    @Autowired
    private AlocacaoRepository alocacaoRepository;

    public void executarCriacao(Alocacao entity) {
        boolean existeDuplicidade = alocacaoRepository
                .existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioId(
                        entity.getTurma().getId(),
                        entity.getDisciplina().getId(),
                        entity.getSala().getId(),
                        entity.getDiaSemana().getId(),
                        entity.getHorario().getId());

        if (existeDuplicidade) {
            throw new BusinessException("Já existe uma alocação cadastrada com os mesmos dados informados.");
        }

    }

    public void executarAtualizacao(Alocacao entity) {
        boolean existeDuplicidade = alocacaoRepository
                .existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioIdAndIdNot(
                        entity.getTurma().getId(),
                        entity.getDisciplina().getId(),
                        entity.getSala().getId(),
                        entity.getDiaSemana().getId(),
                        entity.getHorario().getId(),
                        entity.getId());

        if (existeDuplicidade) {
            throw new BusinessException("Já existe uma alocação cadastrada com os mesmos dados informados.");
        }
    }
}
