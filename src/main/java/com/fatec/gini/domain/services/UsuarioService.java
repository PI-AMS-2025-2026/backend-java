package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.domain.entities.TipoUsuario;
import com.fatec.gini.domain.entities.user.Usuario;
import com.fatec.gini.dto.usuario.UsuarioRequest;
import com.fatec.gini.dto.usuario.UsuarioResponse;
import com.fatec.gini.infrastructure.mappers.UsuarioMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {

        Usuario usuario = UsuarioMapper.toEntity(request);

        if (request.curso() != null) {
            Curso curso = cursoRepository.findById(request.curso().id())
                    .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + request.curso().id()));
            usuario.setCurso(curso);
        }

        usuario.setSenha(encoder.encode(request.senha()));
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setUpdatedAt(LocalDateTime.now());

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
        usuario.setStatus(request.status());
        usuario.setTipoUsuario(request.tipoUsuario());
        

        if (request.curso() != null) {
            Curso curso = cursoRepository.findById(request.curso().id())
                    .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + request.curso().id()));
            usuario.setCurso(curso);
        }

        usuario.setUpdatedAt(LocalDateTime.now());

        return UsuarioMapper.toResponse(repository.save(usuario));
    }

    @Transactional
    public void inativar(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

        usuario.setStatus(Status.INATIVO);
        usuario.setUpdatedAt(LocalDateTime.now());

        repository.save(usuario);
    }
}