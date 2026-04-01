package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Recurso;
import com.fatec.horario.dto.recurso.RecursoRequest;
import com.fatec.horario.dto.recurso.RecursoResponse;
import com.fatec.horario.infrastructure.mappers.RecursoMapper;
import com.fatec.horario.infrastructure.repositories.RecursoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RecursoService {

    @Autowired
    private RecursoRepository repository;

    // Cria um novo recurso
    @Transactional
    public RecursoResponse criar(RecursoRequest request) {
        Recurso entity = RecursoMapper.toEntity(request);
        return RecursoMapper.toResponse(repository.save(entity));
    }

    // Busca por ID
    @Transactional(readOnly = true)
    public RecursoResponse buscarPorId(Long id) {
        Recurso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurso não encontrado"));
        return RecursoMapper.toResponse(entity);

    }

    // Lista com filtros
    @Transactional(readOnly = true)
    public List<RecursoResponse> listar(String nome, String tipo) {

        List<Recurso> lista;

        if (nome != null && tipo != null) {
            lista = repository.findByNomeContainingIgnoreCaseAndTipoContainingIgnoreCase(nome, tipo);
        } else if (nome != null) {
            lista = repository.findByNomeContainingIgnoreCase(nome);
        } else if (tipo != null) {
            lista = repository.findByTipoContainingIgnoreCase(tipo);
        } else {
            lista = repository.findAll();
        }

        return lista.stream()
                .map(RecursoMapper::toResponse)
                .toList();
    }

    // Atualiza um recurso
    @Transactional
    public RecursoResponse atualizar(Long id, RecursoRequest request) {
        Recurso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurso não encontrado"));

        entity.setNome(request.nome());
        entity.setTipo(request.tipo());

        return RecursoMapper.toResponse(repository.save(entity));
    }

    // Deleta um recurso
    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Recurso não encontrado");
        }
        repository.deleteById(id);
    }
}