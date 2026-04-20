package com.fatec.horario.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.entities.HistoricoAlteracao;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.dto.historicoAlteracao.HistoricoAlteracaoRequest;
import com.fatec.horario.dto.historicoAlteracao.HistoricoAlteracaoResponse;
import com.fatec.horario.infrastructure.mappers.HistoricoAlteracaoMapper;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.infrastructure.repositories.HistoricoAlteracaoRepository;
import com.fatec.horario.infrastructure.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class HistoricoAlteracaoService {

	@Autowired
	private HistoricoAlteracaoRepository repository;

	@Autowired
	private AlocacaoRepository alocacaoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Transactional
	public HistoricoAlteracaoResponse criar(HistoricoAlteracaoRequest request) {

		HistoricoAlteracao entity = HistoricoAlteracaoMapper.toEntity(request);

		Alocacao alocacao = buscarAlocacaoPorRequest(request);
		Usuario usuario = buscarUsuarioPorRequest(request);

		entity.setAlocacao(alocacao);
		entity.setUsuario(usuario);

		return HistoricoAlteracaoMapper.toResponse(repository.save(entity));
	}

	@Transactional
	public HistoricoAlteracaoResponse atualizar(Long id, HistoricoAlteracaoRequest request) {

		HistoricoAlteracao entity = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Histórico de alteração não encontrado com ID: " + id));

		Alocacao alocacao = buscarAlocacaoPorRequest(request);
		Usuario usuario = buscarUsuarioPorRequest(request);

		entity.setDataAlteracao(request.dataAlteracao());
		entity.setJustificativa(request.justificativa());
		entity.setCampoAlterado(request.campoAlterado());
		entity.setValorAntigo(request.valorAntigo());
		entity.setValorNovo(request.valorNovo());
		entity.setAlocacao(alocacao);
		entity.setUsuario(usuario);

		return HistoricoAlteracaoMapper.toResponse(repository.save(entity));
	}

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

	@Transactional
	public void deletar(Long id) {

		if (!repository.existsById(id)) {
			throw new EntityNotFoundException("Histórico de alteração não encontrado com ID: " + id);
		}

		repository.deleteById(id);
	}

	private Alocacao buscarAlocacaoPorRequest(HistoricoAlteracaoRequest request) {

		if (request.alocacao() == null) {
			throw new EntityNotFoundException("Alocação não informada para o histórico de alteração.");
		}

		return alocacaoRepository.findById(request.alocacao().id())
				.orElseThrow(() -> new EntityNotFoundException(
						"Alocação não encontrada com ID: " + request.alocacao().id()));
	}

	private Usuario buscarUsuarioPorRequest(HistoricoAlteracaoRequest request) {

		if (request.usuario() == null) {
			throw new EntityNotFoundException("Usuário não informado para o histórico de alteração.");
		}

		return usuarioRepository.findById(request.usuario().id())
				.orElseThrow(() -> new EntityNotFoundException(
						"Usuário não encontrado com ID: " + request.usuario().id()));
	}

}
