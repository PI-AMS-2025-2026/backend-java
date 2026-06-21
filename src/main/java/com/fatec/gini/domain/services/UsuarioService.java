package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.domain.entities.TipoUsuario;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.usuario.UsuarioRequest;
import com.fatec.gini.dto.usuario.UsuarioResponse;
import com.fatec.gini.infrastructure.mappers.UsuarioMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    private final CursoRepository cursoRepository;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {

        Usuario entity = UsuarioMapper.toEntity(request);

        if (request.curso() != null) {
            Curso curso = cursoRepository.findById(request.curso().id())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Curso não encontrado com ID: " + request.curso().id()));
            entity.setCurso(curso);
        }
        String encrypSenha = new BCryptPasswordEncoder().encode(request.senha());

        entity.setSenha(encrypSenha);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return UsuarioMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> listar(
            String nome,
            String email,
            Status status,
            TipoUsuario tipoUsuario,
            int pageNum,
            int size

    ) {
        var pageRequest = PageRequest.of(pageNum, size);
        var page = repository.buscarPorFiltros(
                nome,
                email,
                status,
                tipoUsuario,
                pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(UsuarioMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(UsuarioMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {

        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

        entity.setNome(request.nome());
        entity.setEmail(request.email());
        entity.setStatus(request.status());
        entity.setTipoUsuario(request.tipoUsuario());

        if (request.curso() != null) {
            Curso curso = cursoRepository.findById(request.curso().id())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Curso não encontrado com ID: " + request.curso().id()));
            entity.setCurso(curso);
        }

        entity.setUpdatedAt(LocalDateTime.now());

        return UsuarioMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void inativar(Long id) {
        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

        entity.setStatus(Status.INATIVO);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);
    }

    @Transactional
    public boolean jaUsuarioExisteEmail(String email) {
        return this.repository.findByEmail(email) != null;
    }
}