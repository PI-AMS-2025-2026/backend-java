package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.professor.ProfessorRequest;
import com.fatec.gini.dto.professor.ProfessorResponse;
import com.fatec.gini.infrastructure.mappers.ProfessorMapper;
import com.fatec.gini.infrastructure.repositories.ProfessorRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository repository;

    @Transactional
    public ProfessorResponse criar(ProfessorRequest request) {

        Professor professor = ProfessorMapper.toEntity(request);

        professor.setCreatedAt(LocalDateTime.now());
        professor.setUpdatedAt(LocalDateTime.now());

        return ProfessorMapper.toResponse(repository.save(professor));
    }

    @Transactional(readOnly = true)
    public PageResponse<ProfessorResponse> listar(
            String nome,
            String email,
            String cidade,
            Status status,
            int pageNum,
            int size

    ) {
        var pageRequest = PageRequest.of(pageNum, size);
        var page = repository.buscarPorFiltros(
                nome,
                email,
                cidade,
                status,
                pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(ProfessorMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional(readOnly = true)
    public ProfessorResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(ProfessorMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com ID: " + id));
    }

    @Transactional
    public ProfessorResponse atualizar(Long id, ProfessorRequest request) {

        Professor professor = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com ID: " + id));

        professor.setNome(request.nome());
        professor.setEmail(request.email());
        professor.setStatus(request.status());

        professor.setUpdatedAt(LocalDateTime.now());

        return ProfessorMapper.toResponse(repository.save(professor));
    }

    @Transactional
    public void inativar(Long id) {
        Professor professor = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com ID: " + id));

        professor.setStatus(Status.INATIVO);
        professor.setUpdatedAt(LocalDateTime.now());

        repository.save(professor);
    }

}
