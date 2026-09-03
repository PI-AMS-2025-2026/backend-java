package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.infrastructure.mappers.DisponibilidadeProfessorMapper;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisponibilidadeProfessorService {

    private final DisponibilidadeProfessorRepository repository;
    private final ProfessorRepository professorRepository;
    private final BlocoHorarioRepository blocoHorarioRepository;

    @Transactional
    public DisponibilidadeProfessorResponse criar(DisponibilidadeProfessorRequest request) {

        Professor professor = professorRepository.findById(request.professor().id())
                .orElseThrow(() -> new EntityNotFoundException(
                "Professor não encontrado com ID: " + request.professor().id()));

        BlocoHorario blocoHorario = blocoHorarioRepository.findById(request.blocoHorario().id())
                .orElseThrow(() -> new EntityNotFoundException(
                "Horário não encontrado com ID: " + request.blocoHorario().id()));

        // CORREÇÃO: impede cadastro duplicado de disponibilidade para o mesmo professor, dia e bloco de horário
        if (repository.existsByProfessorIdAndDiaSemanaAndBlocoHorarioId(
                professor.getId(), request.diaSemana(), blocoHorario.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe uma disponibilidade cadastrada para este professor neste dia e bloco de horário.");
        }

        DisponibilidadeProfessor entity = DisponibilidadeProfessorMapper.toEntity(request);

        entity.setProfessor(professor);
        entity.setBlocoHorario(blocoHorario);

        LocalDateTime agora = LocalDateTime.now();
        entity.setCreatedAt(agora);
        entity.setUpdatedAt(agora);

        repository.save(entity);

        return DisponibilidadeProfessorMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public DisponibilidadeProfessorResponse buscarPorId(Long id) {

        DisponibilidadeProfessor disponibilidade = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                "Disponibilidade não encontrada com ID: " + id));
        return DisponibilidadeProfessorMapper.toResponse(disponibilidade);
    }

    @Transactional(readOnly = true)
    public PageResponse<DisponibilidadeProfessorResponse> listar(
            Long professor,
            DiaSemana diaSemana,
            Long blocoHorario,
            int pageNum,
            int size) {

        var pageRequest = PageRequest.of(pageNum, size);

        var page = repository.buscarComFiltros(
                professor,
                diaSemana,
                blocoHorario,
                pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(DisponibilidadeProfessorMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public DisponibilidadeProfessorResponse atualizar(Long id, DisponibilidadeProfessorRequest request) {

        DisponibilidadeProfessor entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                "Disponibilidade não encontrada com ID: " + id));

        Professor professor = professorRepository.findById(request.professor().id())
                .orElseThrow(() -> new EntityNotFoundException(
                "Professor não encontrado com ID: " + request.professor().id()));

        BlocoHorario blocoHorario = blocoHorarioRepository.findById(request.blocoHorario().id())
                .orElseThrow(() -> new EntityNotFoundException(
                "Horário não encontrado com ID: " + request.blocoHorario().id()));

        // CORREÇÃO: mesma validação de duplicidade ao atualizar, ignorando o próprio registro sendo editado
        if (repository.existsByProfessorIdAndDiaSemanaAndBlocoHorarioIdAndIdNot(
                professor.getId(), request.diaSemana(), blocoHorario.getId(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe outra disponibilidade cadastrada para este professor neste dia e bloco de horário.");
        }

        entity.setProfessor(professor);
        entity.setBlocoHorario(blocoHorario);
        entity.setDiaSemana(request.diaSemana());

        entity.setUpdatedAt(LocalDateTime.now());
        
        // CORREÇÃO: removida a chamada duplicada a repository.save(entity) que existia neste método

        repository.save(entity);

        return DisponibilidadeProfessorMapper.toResponse(entity);
    }

    @Transactional
    public void deletar(Long id) {
        DisponibilidadeProfessor disponibilidade = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                "Disponibilidade não encontrada com ID: " + id));

        repository.delete(disponibilidade);
    }
}