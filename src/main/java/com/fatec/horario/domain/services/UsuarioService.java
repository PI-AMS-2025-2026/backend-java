package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.TipoUsuario;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.dto.usuario.UsuarioRequest;
import com.fatec.horario.dto.usuario.UsuarioResponse;
import com.fatec.horario.infrastructure.mappers.UsuarioMapper;
import com.fatec.horario.infrastructure.repositories.TipoUsuarioRepository;
import com.fatec.horario.infrastructure.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TipoUsuarioRepository tipoRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest dto) {
        TipoUsuario tipo = tipoRepository.findById(dto.id_tipo_usuario())
                .orElseThrow(() -> new EntityNotFoundException(
                        "TipoUsuario não encontrado com ID: " + dto.id_tipo_usuario()));

        Usuario usuario = UsuarioMapper.toEntity(dto, tipo);

        usuario.setSenha(encoder.encode(dto.senha()));
        usuario.setCreated_at(LocalDateTime.now());
        usuario.setUpdated_at(LocalDateTime.now());

        return UsuarioMapper.toResponse(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(
            String nome,
            String email,
            String cidade,
            String status,
            Long tipoUsuarioId,
            int page,
            int size

    ) {
        var pageRequest = PageRequest.of(page, size);
        var pageUsuario = repository.buscarPorFiltros(nome, email, cidade, status, tipoUsuarioId, pageRequest);

        return pageUsuario.map(UsuarioMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(UsuarioMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest dto) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

        TipoUsuario tipo = tipoRepository.findById(dto.id_tipo_usuario())
                .orElseThrow(() -> new EntityNotFoundException(
                        "TipoUsuario não encontrado com ID: " + dto.id_tipo_usuario()));

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setCidade(dto.cidade());
        usuario.setStatus(dto.status());
        usuario.setTipo_usuario(tipo);
        usuario.setUpdated_at(LocalDateTime.now());

        return UsuarioMapper.toResponse(repository.save(usuario));
    }

    @Transactional
    public void inativar(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

        usuario.setStatus(false);
        usuario.setUpdated_at(LocalDateTime.now());

        repository.save(usuario);
    }
}