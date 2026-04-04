package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.TipoSala;
import com.fatec.horario.dto.tipoSala.TipoSalaRequest;
import com.fatec.horario.dto.tipoSala.TipoSalaResponse;
import com.fatec.horario.infrastructure.mappers.TipoSalaMapper;
import com.fatec.horario.infrastructure.repositories.TipoSalaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TipoSalaService {

    @Autowired
    private TipoSalaRepository repository;

    // Lista todos ou filtra por nome
    @Transactional(readOnly = true)
    public List<TipoSalaResponse> listar(String nome) {

        List<TipoSala> lista = repository.buscarPorFiltro(nome);

        return lista.stream()
                .map(TipoSalaMapper::toResponse)
                .toList();
    }

    // Cria um novo tipo de sala
    @Transactional
    public TipoSalaResponse criar(TipoSalaRequest request) {
        TipoSala entity = TipoSalaMapper.toEntity(request);
        return TipoSalaMapper.toResponse(repository.save(entity));
    }

    // Busca por ID com tratamento de erro
    @Transactional(readOnly = true)
    public TipoSalaResponse buscarPorId(Long id) {
        TipoSala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de sala não encontrado com ID: " + id));
        return TipoSalaMapper.toResponse(entity);
    }

    // Atualiza um tipo de sala existente
    @Transactional
    public TipoSalaResponse atualizar(Long id, TipoSalaRequest request) {
        TipoSala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de sala não encontrado com ID: " + id));

        entity.setNome(request.nome());

        return TipoSalaMapper.toResponse(repository.save(entity));
    }

    // Remove um tipo de sala
    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Tipo de sala não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}