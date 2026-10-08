package com.fatec.gini.domain.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.HistoricoVersaoAlocacao;
import com.fatec.gini.dto.historicoVersaoAlocacao.HistoricoVersaoAlocacaoResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.infrastructure.mappers.HistoricoVersaoAlocacaoMapper;
import com.fatec.gini.infrastructure.repositories.HistoricoVersaoAlocacaoRepository;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoricoVersaoAlocacaoService {

	private final HistoricoVersaoAlocacaoRepository repository;
	private final ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;

	@Transactional(readOnly = true)
	public PageResponse<HistoricoVersaoAlocacaoResponse> listar(Long idAlocacao, Long idUsuario, int pageNum,
			int size) {

		var pageRequest = PageRequest.of(pageNum, size);
		var page = repository.buscarPorFiltros(idAlocacao, idUsuario,
				validarAutorizacaoCurso.cursoParaFiltro(null), pageRequest);

		return new PageResponse<>(
				page.getContent().stream().map(HistoricoVersaoAlocacaoMapper::toResponse).toList(),
				page.getNumber(),
				page.getSize(),
				page.getNumberOfElements(),
				page.getTotalPages());
	}

	@Transactional(readOnly = true)
	public HistoricoVersaoAlocacaoResponse buscarPorId(Long id) {

		HistoricoVersaoAlocacao entity = repository.findById(id)
				.orElseThrow(
						() -> new EntityNotFoundException("Histórico versão de alocação não encontrado com ID: " + id));
		validarAutorizacaoCurso.validarCurso(entity.getAlocacao().getQuadroHorario().getCurso());

		return HistoricoVersaoAlocacaoMapper.toResponse(entity);
	}

}