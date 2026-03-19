package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.TipoUsuario;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.dto.usuario.UsuarioRequest;
import com.fatec.horario.dto.usuario.UsuarioResponse;
import com.fatec.horario.infrastructure.repositories.UsuarioRepository;
import com.fatec.horario.infrastructure.mappers.UsuarioMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest dto, TipoUsuario tipo) {

        Usuario usuario = UsuarioMapper.toEntity(dto, tipo);

        usuario.setSenha(encoder.encode(dto.senha()));
        usuario.setCreated_at(LocalDateTime.now());
        usuario.setUpdated_at(LocalDateTime.now());

        return UsuarioMapper.toResponse(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(UsuarioMapper::toResponse)
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(String nome, String email, Boolean status, Pageable pageable) {

        Page<Usuario> page;

        if (nome != null) {
            page = repository.findByNomeContainingIgnoreCase(nome, pageable);
        } else if (email != null) {
            page = repository.findByEmailContainingIgnoreCase(email, pageable);
        } else if (status != null) {
            page = repository.findByStatus(status, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        return page.map(UsuarioMapper::toResponse);
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest dto, TipoUsuario tipo) {
        Usuario usuario = repository.findById(id).orElseThrow();

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setStatus(dto.status());
        usuario.setTipo_usuario(tipo);
        usuario.setUpdated_at(LocalDateTime.now());

        return UsuarioMapper.toResponse(repository.save(usuario));
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = repository.findById(id).orElseThrow();
        usuario.setStatus(false);
        repository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return repository.findByEmail(email).orElseThrow();
    }
}