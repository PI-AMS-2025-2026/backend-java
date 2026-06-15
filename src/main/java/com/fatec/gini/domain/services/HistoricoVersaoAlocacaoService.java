package com.fatec.gini.domain.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.HistoricoVersaoAlocacao;
import com.fatec.gini.dto.historicoVersaoAlocacao.HistoricoVersaoAlocacaoResponse;
import com.fatec.gini.infrastructure.mappers.HistoricoVersaoAlocacaoMapper;
import com.fatec.gini.infrastructure.repositories.HistoricoVersaoAlocacaoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoricoVersaoAlocacaoService {

	private final HistoricoVersaoAlocacaoRepository repository;

	@Transactional(readOnly = true)
	public Page<HistoricoVersaoAlocacaoResponse> listar(Long idAlocacao, Long idUsuario, int page, int size) {

		var pageRequest = PageRequest.of(page, size);
		var pageHistorico = repository.buscarPorFiltros(idAlocacao, idUsuario, pageRequest);

		return pageHistorico.map(HistoricoVersaoAlocacaoMapper::toResponse);
	}

	@Transactional(readOnly = true)
	public HistoricoVersaoAlocacaoResponse buscarPorId(Long id) {

		HistoricoVersaoAlocacao entity = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Histórico versão de alocação não encontrado com ID: " + id));

		return HistoricoVersaoAlocacaoMapper.toResponse(entity);
	}

}
