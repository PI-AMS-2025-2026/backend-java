package com.fatec.gini.domain.services;


import org.springframework.data.domain.PageRequest;
        import org.springframework.security.access.AccessDeniedException;
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
import com.fatec.gini.infrastructure.repositories.ProfessorDisciplinaRepository;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisponibilidadeProfessorService {

    private final DisponibilidadeProfessorRepository repository;
    private final ProfessorRepository professorRepository;
    private final BlocoHorarioRepository blocoHorarioRepository;
        private final ProfessorDisciplinaRepository professorDisciplinaRepository;
        private final ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;

    @Transactional
    public DisponibilidadeProfessorResponse criar(DisponibilidadeProfessorRequest request) {

        Professor professor = professorRepository.findById(request.professor().id())
                .orElseThrow(() -> new EntityNotFoundException(
                "Professor não encontrado com ID: " + request.professor().id()));
        validarProfessor(professor.getId());

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

        repository.save(entity);

        return DisponibilidadeProfessorMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public DisponibilidadeProfessorResponse buscarPorId(Long id) {

        DisponibilidadeProfessor disponibilidade = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                "Disponibilidade não encontrada com ID: " + id));
        validarProfessor(disponibilidade.getProfessor().getId());
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
                validarAutorizacaoCurso.cursoParaFiltro(null),
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
        validarProfessor(entity.getProfessor().getId());

        Professor professor = professorRepository.findById(request.professor().id())
                .orElseThrow(() -> new EntityNotFoundException(
                "Professor não encontrado com ID: " + request.professor().id()));
        validarProfessor(professor.getId());

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


        // CORREÇÃO: removida a chamada duplicada a repository.save(entity) que existia neste método

        repository.save(entity);

        return DisponibilidadeProfessorMapper.toResponse(entity);
    }

    @Transactional
    public void deletar(Long id) {
        DisponibilidadeProfessor disponibilidade = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                "Disponibilidade não encontrada com ID: " + id));
        validarProfessor(disponibilidade.getProfessor().getId());

        repository.delete(disponibilidade);
        }

        private void validarProfessor(Long professorId) {
                Long cursoId = validarAutorizacaoCurso.cursoParaFiltro(null);
                if (cursoId != null && !professorDisciplinaRepository
                                .existsByProfessorIdAndDisciplinaCursoId(professorId, cursoId)) {
                        throw new AccessDeniedException(
                                        "Professor não pertence ao curso do usuário");
                }
        }
}
