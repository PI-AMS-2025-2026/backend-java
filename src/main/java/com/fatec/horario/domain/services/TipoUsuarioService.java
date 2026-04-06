package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.TipoUsuario;
import com.fatec.horario.dto.tipoUsuario.TipoUsuarioRequest;
import com.fatec.horario.dto.tipoUsuario.TipoUsuarioResponse;
import com.fatec.horario.infrastructure.repositories.TipoUsuarioRepository;
import com.fatec.horario.infrastructure.mappers.TipoUsuarioMapper;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TipoUsuarioService {

    @Autowired
    private TipoUsuarioRepository repository;

    @Transactional
    public TipoUsuarioResponse criar(TipoUsuarioRequest dto) {
        TipoUsuario tipo = TipoUsuarioMapper.toEntity(dto);
        return TipoUsuarioMapper.toResponse(repository.save(tipo));
    }

    @Transactional(readOnly = true)
    public TipoUsuarioResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(TipoUsuarioMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de usuário não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<TipoUsuarioResponse> listar(String nome) {
        return repository.buscarPorFiltro(nome)
                .stream()
                .map(TipoUsuarioMapper::toResponse)
                .toList();
    }

    @Transactional
    public TipoUsuarioResponse atualizar(Long id, TipoUsuarioRequest dto) {
        TipoUsuario tipo = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de usuário não encontrado com ID: " + id));

        tipo.setNome(dto.nome());

        return TipoUsuarioMapper.toResponse(repository.save(tipo));
    }

    @Transactional
    public void deletar(Long id) {
        TipoUsuario tipo = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de usuário não encontrado com ID: " + id));

        repository.delete(tipo);
    }
}