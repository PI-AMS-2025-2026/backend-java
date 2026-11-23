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
import com.fatec.gini.dto.disponibilidadeProfessor.GradeDisponibilidadeProfessorRequest;
import com.fatec.gini.dto.disponibilidadeProfessor.HorarioDisponibilidadeProfessorRequest;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.infrastructure.mappers.DisponibilidadeProfessorMapper;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorDisciplinaRepository;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Transactional
    public List<DisponibilidadeProfessorResponse> sincronizarGrade(
            Long professorId,
            GradeDisponibilidadeProfessorRequest request) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com ID: " + professorId));
        validarProfessor(professorId);

        List<HorarioDisponibilidadeProfessorRequest> horarios = request.horarios();
        Set<ChaveHorario> desejados = new HashSet<>();
        Set<Long> idsBlocos = new HashSet<>();
        for (HorarioDisponibilidadeProfessorRequest horario : horarios) {
            if (!desejados.add(new ChaveHorario(horario.diaSemana(), horario.blocoHorarioId()))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A grade contém um horário duplicado para o mesmo dia.");
            }
            idsBlocos.add(horario.blocoHorarioId());
        }

        Map<Long, BlocoHorario> blocosPorId = new HashMap<>();
        blocoHorarioRepository.findAllById(idsBlocos).forEach(bloco -> blocosPorId.put(bloco.getId(), bloco));
        if (blocosPorId.size() != idsBlocos.size()) {
            Set<Long> ausentes = new HashSet<>(idsBlocos);
            ausentes.removeAll(blocosPorId.keySet());
            throw new EntityNotFoundException("Bloco(s) de horário não encontrado(s): " + ausentes);
        }

        List<DisponibilidadeProfessor> existentes = repository.findByProfessorId(professorId);
        Map<ChaveHorario, DisponibilidadeProfessor> atuaisPorChave = new HashMap<>();
        existentes.forEach(disponibilidade -> atuaisPorChave.put(
                new ChaveHorario(disponibilidade.getDiaSemana(), disponibilidade.getBlocoHorario().getId()),
                disponibilidade));

        List<DisponibilidadeProfessor> remover = existentes.stream()
                .filter(disponibilidade -> !desejados.contains(
                        new ChaveHorario(disponibilidade.getDiaSemana(), disponibilidade.getBlocoHorario().getId())))
                .toList();
        List<DisponibilidadeProfessor> adicionar = horarios.stream()
                .filter(horario -> !atuaisPorChave.containsKey(
                        new ChaveHorario(horario.diaSemana(), horario.blocoHorarioId())))
                .map(horario -> new DisponibilidadeProfessor(
                        professor,
                        horario.diaSemana(),
                        blocosPorId.get(horario.blocoHorarioId())))
                .toList();

        repository.deleteAll(remover);
        repository.saveAll(adicionar);
        return repository.findByProfessorId(professorId).stream()
                .map(DisponibilidadeProfessorMapper::toResponse)
                .toList();
    }

    private record ChaveHorario(DiaSemana diaSemana, Long blocoHorarioId) {
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
            String professorNome,
            String horario,
            String busca,
            int pageNum,
            int size) {

        var pageRequest = PageRequest.of(pageNum, size);

        var page = repository.buscarComFiltros(
                professor,
                diaSemana,
                blocoHorario,
                professorNome,
                horario,
                busca,
                validarAutorizacaoCurso.cursoParaFiltro(null),
                pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(DisponibilidadeProfessorMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
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
                                        "Professor não pertence ao curso do usuário.");
                }
        }
}
