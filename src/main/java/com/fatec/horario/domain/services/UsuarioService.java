package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.TipoUsuario;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.dto.usuario.UsuarioRequest;
import com.fatec.horario.dto.usuario.UsuarioResponse;
import com.fatec.horario.infrastructure.mappers.UsuarioMapper;
import com.fatec.horario.infrastructure.repositories.TipoUsuarioRepository;
import com.fatec.horario.infrastructure.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoRepository;
    private final BCryptPasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          TipoUsuarioRepository tipoRepository,
                          BCryptPasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.tipoRepository = tipoRepository;
        this.encoder = encoder;
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest dto) {

        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email já cadastrado");
        }

        TipoUsuario tipo = tipoRepository.findById(dto.id_tipo_usuario())
                .orElseThrow(() -> new EntityNotFoundException("TipoUsuario não encontrado"));

        Usuario usuario = UsuarioMapper.toEntity(dto, tipo);

        usuario.setSenha(encoder.encode(dto.senha()));
        usuario.setCreated_at(LocalDateTime.now());
        usuario.setUpdated_at(LocalDateTime.now());

        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(
            String nome,
            String email,
            Boolean status,
            Long tipoUsuarioId,
            Pageable pageable
    ) {

        Specification<Usuario> spec = Specification.where(null);

        if (nome != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
        }

        if (email != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }

        if (tipoUsuarioId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("tipo_usuario").get("id_tipo_usuario"), tipoUsuarioId));
        }

        return usuarioRepository.findAll(spec, pageable)
                .map(UsuarioMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(UsuarioMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest dto) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        TipoUsuario tipo = tipoRepository.findById(dto.id_tipo_usuario())
                .orElseThrow(() -> new EntityNotFoundException("TipoUsuario não encontrado"));

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setCidade(dto.cidade());
        usuario.setStatus(dto.status());
        usuario.setTipo_usuario(tipo);
        usuario.setUpdated_at(LocalDateTime.now());

        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        usuario.setStatus(false);
        usuario.setUpdated_at(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }
}