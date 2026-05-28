package com.fatec.gini.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.services.usecase.write.AtualizarAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.write.CriarAlocacaoUseCase;
import com.fatec.gini.dto.alocacao.AlocacaoRequest;
import com.fatec.gini.dto.alocacao.AlocacaoResponse;
import com.fatec.gini.infrastructure.mappers.AlocacaoMapper;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AlocacaoService {

        @Autowired
        private AlocacaoRepository repository;

        @Autowired
        private CriarAlocacaoUseCase criarAlocacaoUseCase;

        @Autowired
        private AtualizarAlocacaoUseCase atualizarAlocacaoUseCase;



        public AlocacaoResponse criar(AlocacaoRequest request) {

                Alocacao entity = AlocacaoMapper.toEntity(request);

                return AlocacaoMapper.toResponse(
                                criarAlocacaoUseCase.executar(
                                                entity,
                                                request.usuarioAlteracao().id()));
        }

        public AlocacaoResponse atualizar(Long id, AlocacaoRequest request) {

                Alocacao entity = AlocacaoMapper.toEntity(request);

                String justificativaAlteracao = request.justificativaAlteracao();

                return AlocacaoMapper.toResponse(
                                atualizarAlocacaoUseCase.executar(
                                                id,
                                                entity,
                                                request.usuarioAlteracao().id(),
                                                justificativaAlteracao));
        }

        @Transactional(readOnly = true)
        public Page<AlocacaoResponse> listar(
                        Long turmaId, Long disciplinaId, Long salaId, Long usuarioId,
                        Long diaSemanaId, Long horarioId, Long gradeId, int page,
                        int size) {

                var pageRequest = PageRequest.of(page, size);

                var pageAlocacao = repository.buscarPorFiltros(
                                turmaId, disciplinaId, salaId, usuarioId,
                                diaSemanaId, horarioId, gradeId, pageRequest);

                return pageAlocacao.map(AlocacaoMapper::toResponse);
        }

        @Transactional(readOnly = true)
        public AlocacaoResponse buscarPorId(Long id) {
                return repository.findById(id)
                                .map(AlocacaoMapper::toResponse)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Alocação não encontrada com ID: " + id));
        }

        @Transactional
        public void deletar(Long id) {
                if (!repository.existsById(id)) {
                        throw new EntityNotFoundException(
                                        "Alocação não encontrada com ID: " + id);
                }
                repository.deleteById(id);
        }
}