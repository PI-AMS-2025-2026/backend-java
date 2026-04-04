package com.fatec.horario.domain.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Status;
import com.fatec.horario.domain.entities.TipoUsuario;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.dto.usuario.UsuarioRequest;
import com.fatec.horario.dto.usuario.UsuarioResponse;
import com.fatec.horario.infrastructure.mappers.UsuarioMapper;
import com.fatec.horario.infrastructure.repositories.TipoUsuarioRepository;
import com.fatec.horario.infrastructure.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TipoUsuarioRepository tipoRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {

        Usuario usuario = UsuarioMapper.toEntity(request);

        if (request.tipoUsuario() != null) {
            TipoUsuario tipo = tipoRepository.findById(request.tipoUsuario().id())
                    .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de usuário não encontrado com ID: " + request.tipoUsuario().id()));
            usuario.setTipo_usuario(tipo);
        }
        usuario.setSenha(encoder.encode(request.senha()));
        usuario.setCreated_at(LocalDateTime.now());
        usuario.setUpdated_at(LocalDateTime.now());

        return UsuarioMapper.toResponse(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(
            String nome,
            String email,
            String cidade,
            Status status,
            Long tipoUsuarioId,
            String tipoUsuarioNome,
            int page,
            int size

    ) {
        var pageRequest = PageRequest.of(page, size);
        var pageUsuario = repository.buscarPorFiltros(
            nome,
            email,
            cidade,
            status,
            tipoUsuarioId,
            tipoUsuarioNome,
            pageRequest);

        return pageUsuario.map(UsuarioMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(UsuarioMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setCidade(request.cidade());
        usuario.setStatus(request.status());
        if (request.tipoUsuario() != null) {
            TipoUsuario tipo = tipoRepository.findById(request.tipoUsuario().id())
                    .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de usuário não encontrado com ID: " + request.tipoUsuario().id()));

            usuario.setTipo_usuario(tipo);
        }
        usuario.setUpdated_at(LocalDateTime.now());

        return UsuarioMapper.toResponse(repository.save(usuario));
    }

    @Transactional
    public void inativar(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

        usuario.setStatus(Status.INATIVO);
        usuario.setUpdated_at(LocalDateTime.now());

        repository.save(usuario);
    }
}