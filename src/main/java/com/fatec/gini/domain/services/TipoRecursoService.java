package com.fatec.gini.domain.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.TipoRecurso;
import com.fatec.gini.dto.tipoRecurso.TipoRecursoRequest;
import com.fatec.gini.dto.tipoRecurso.TipoRecursoResponse;
import com.fatec.gini.infrastructure.mappers.TipoRecursoMapper;
import com.fatec.gini.infrastructure.repositories.TipoRecursoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoRecursoService {

    private final TipoRecursoRepository repository;

    // Lista todos ou filtra por nome
    @Transactional(readOnly = true)
    public List<TipoRecursoResponse> listar(String nome) {

        List<TipoRecurso> lista = repository.buscarPorFiltro(nome);

        return lista.stream()
                .map(TipoRecursoMapper::toResponse)
                .toList();
    }

    // Cria um novo tipo de recurso
    @Transactional
    public TipoRecursoResponse criar(TipoRecursoRequest request) {
        TipoRecurso entity = TipoRecursoMapper.toEntity(request);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return TipoRecursoMapper.toResponse(repository.save(entity));
    }

    // Busca por ID com tratamento de erro
    @Transactional(readOnly = true)
    public TipoRecursoResponse buscarPorId(Long id) {
        TipoRecurso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de recurso não encontrado com ID: " + id));
        return TipoRecursoMapper.toResponse(entity);
    }

    // Atualiza um tipo de recurso existente
    @Transactional
    public TipoRecursoResponse atualizar(Long id, TipoRecursoRequest request) {
        TipoRecurso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                "Tipo de recurso não encontrado com ID: " + id));

        entity.setNome(request.nome());
        entity.setUpdatedAt(LocalDateTime.now()); // Alteração: atualiza somente a data de alteração.

        return TipoRecursoMapper.toResponse(repository.save(entity));
    }

    // Remove um tipo de recurso
    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Tipo de recurso não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
