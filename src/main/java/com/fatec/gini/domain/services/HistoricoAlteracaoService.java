package com.fatec.gini.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.HistoricoAlteracao;
import com.fatec.gini.dto.historicoAlteracao.HistoricoAlteracaoResponse;
import com.fatec.gini.infrastructure.mappers.HistoricoAlteracaoMapper;
import com.fatec.gini.infrastructure.repositories.HistoricoAlteracaoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class HistoricoAlteracaoService {

	@Autowired
	private HistoricoAlteracaoRepository repository;

	@Transactional(readOnly = true)
	public Page<HistoricoAlteracaoResponse> listar(Long idAlocacao, Long idUsuario, int page, int size) {

		var pageRequest = PageRequest.of(page, size);
		var pageHistorico = repository.buscarPorFiltros(idAlocacao, idUsuario, pageRequest);

		return pageHistorico.map(HistoricoAlteracaoMapper::toResponse);
	}

	@Transactional(readOnly = true)
	public HistoricoAlteracaoResponse buscarPorId(Long id) {

		HistoricoAlteracao entity = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Histórico de alteração não encontrado com ID: " + id));

		return HistoricoAlteracaoMapper.toResponse(entity);
	}

}
