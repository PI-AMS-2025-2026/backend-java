package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorResponse;
import com.fatec.gini.infrastructure.mappers.DisponibilidadeProfessorMapper;
import com.fatec.gini.infrastructure.repositories.DiaSemanaRepository;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisponibilidadeProfessorService {

        private final DisponibilidadeProfessorRepository repository;
        private final ProfessorRepository professorRepository;
        private final DiaSemanaRepository diaSemanaRepository;
        private final BlocoHorarioRepository blocoHorarioRepository;

        @Transactional
        public DisponibilidadeProfessorResponse criar(DisponibilidadeProfessorRequest request) {
                /*
                 * TO: Validação no local errado colocar em um service específico de regras de
                 * negócio
                 * if (repository.existsByProfessorIdAndDiaSemanaIdAndBlocoHorarioId(
                 * request.idProfessor(), request.idDiaSemana(), request.idBlocoHorario())) {
                 * throw new RuntimeException(
                 * "Disponibilidade já cadastrada para este professor neste dia e horário.");
                 * }
                 */

                Professor professor = professorRepository.findById(request.professor().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Professor não encontrado com ID: " + request.professor().id()));

                DiaSemana dia = diaSemanaRepository.findById(request.diaSemana().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Dia da semana não encontrado com ID: " + request.diaSemana().id()));

                BlocoHorario blocoHorario = blocoHorarioRepository.findById(request.blocoHorario().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Horário não encontrado com ID: " + request.blocoHorario().id()));

                DisponibilidadeProfessor entity = DisponibilidadeProfessorMapper.toEntity(request);
                entity.setProfessor(professor);
                entity.setDiaSemana(dia);
                entity.setBlocoHorario(blocoHorario);
                entity.setCreatedAt(LocalDateTime.now());
                entity.setUpdatedAt(LocalDateTime.now());
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
        public Page<DisponibilidadeProfessorResponse> listar(
                        Long professor,
                        Long diaSemana,
                        Long blocoHorario,
                        int page, int size) {

                var pageRequest = PageRequest.of(page, size);

                var pageDisponibilidade = repository.buscarComFiltros(
                                professor,
                                diaSemana,
                                blocoHorario,
                                pageRequest);

                return pageDisponibilidade.map(DisponibilidadeProfessorMapper::toResponse);
        }

        @Transactional
        public DisponibilidadeProfessorResponse atualizar(Long id, DisponibilidadeProfessorRequest request) {

                DisponibilidadeProfessor entity = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Disponibilidade não encontrada com ID: " + id));

                Professor professor = professorRepository.findById(request.professor().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Professor não encontrado com ID: " + request.professor().id()));

                DiaSemana dia = diaSemanaRepository.findById(request.diaSemana().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Dia da semana não encontrado com ID: " + request.diaSemana().id()));

                BlocoHorario blocoHorario = blocoHorarioRepository.findById(request.blocoHorario().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Horário não encontrado com ID: " + request.blocoHorario().id()));

                entity.setProfessor(professor);
                entity.setDiaSemana(dia);
                entity.setBlocoHorario(blocoHorario);

                entity.setUpdatedAt(LocalDateTime.now());
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