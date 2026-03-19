package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.TipoUsuario;
import com.fatec.horario.dto.tipoUsuario.TipoUsuarioRequest;
import com.fatec.horario.dto.tipoUsuario.TipoUsuarioResponse;
import com.fatec.horario.infrastructure.repositories.TipoUsuarioRepository;
import com.fatec.horario.infrastructure.mappers.TipoUsuarioMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TipoUsuarioService {

    private final TipoUsuarioRepository repository;

    @Transactional
    public TipoUsuarioResponse criar(TipoUsuarioRequest dto) {
        TipoUsuario tipo = TipoUsuarioMapper.toEntity(dto);
        return TipoUsuarioMapper.toResponse(repository.save(tipo));
    }

    @Transactional(readOnly = true)
    public TipoUsuarioResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(TipoUsuarioMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("TipoUsuario não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<TipoUsuarioResponse> listar(Pageable pageable) {
        return repository.findAll(pageable)
                .map(TipoUsuarioMapper::toResponse);
    }

    @Transactional
    public TipoUsuarioResponse atualizar(Long id, TipoUsuarioRequest dto) {
        TipoUsuario tipo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoUsuario não encontrado"));

        tipo.setNome(dto.nome());

        return TipoUsuarioMapper.toResponse(repository.save(tipo));
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}