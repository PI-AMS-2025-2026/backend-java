package com.fatec.horario.domain.services.usecase.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.web.exception.BusinessException;

@Service
public class DuplicidadeAlocacaoUseCase {

	@Autowired
	private AlocacaoRepository alocacaoRepository;

	public void validarNaoExisteDuplicidadeParaCriacao(Alocacao alocacao) {

		boolean existeDuplicidade = alocacaoRepository
				.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioId(
						alocacao.getTurma().getId(),
						alocacao.getDisciplina().getId(),
						alocacao.getSala().getId(),
						alocacao.getDiaSemana().getId(),
						alocacao.getHorario().getId());

		if (existeDuplicidade) {
			throw new BusinessException("Já existe uma alocação cadastrada com os mesmos dados informados.");
		}
	}

	public void validarNaoExisteDuplicidadeParaAtualizacao(Alocacao alocacao) {

		boolean existeDuplicidade = alocacaoRepository
				.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioIdAndIdNot(
						alocacao.getTurma().getId(),
						alocacao.getDisciplina().getId(),
						alocacao.getSala().getId(),
						alocacao.getDiaSemana().getId(),
						alocacao.getHorario().getId(),
						alocacao.getId());

		if (existeDuplicidade) {
			throw new BusinessException("Já existe uma alocação cadastrada com os mesmos dados informados.");
		}
	}
}
