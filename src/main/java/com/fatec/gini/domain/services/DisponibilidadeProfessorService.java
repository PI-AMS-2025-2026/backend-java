package com.fatec.gini.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.domain.entities.Horario;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorResponse;
import com.fatec.gini.infrastructure.mappers.DisponibilidadeProfessorMapper;
import com.fatec.gini.infrastructure.repositories.DiaSemanaRepository;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.infrastructure.repositories.HorarioRepository;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DisponibilidadeProfessorService {

        @Autowired
        private DisponibilidadeProfessorRepository repository;
        @Autowired
        private UsuarioRepository usuarioRepository;
        @Autowired
        private DiaSemanaRepository diaSemanaRepository;
        @Autowired
        private HorarioRepository horarioRepository;

        @Transactional
        public DisponibilidadeProfessorResponse criar(DisponibilidadeProfessorRequest request) {
                /*
                 * TODO: Validação no local errado colocar em um service específico de regras de
                 * negócio
                 * if (repository.existsByUsuarioIdAndDiaSemanaIdAndHorarioId(
                 * request.idUsuario(), request.idDiaSemana(), request.idHorario())) {
                 * throw new RuntimeException(
                 * "Disponibilidade já cadastrada para este professor neste dia e horário.");
                 * }
                 */

                Usuario usuario = usuarioRepository.findById(request.usuario().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Usuário não encontrado com ID: " + request.usuario().id()));

                DiaSemana dia = diaSemanaRepository.findById(request.diaSemana().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Dia da semana não encontrado com ID: " + request.diaSemana().id()));

                Horario horario = horarioRepository.findById(request.horario().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Horário não encontrado com ID: " + request.horario().id()));

                DisponibilidadeProfessor disponibilidade = DisponibilidadeProfessorMapper.toEntity(request);
                disponibilidade.setUsuario(usuario);
                disponibilidade.setDiaSemana(dia);
                disponibilidade.setHorario(horario);

                repository.save(disponibilidade);

                return DisponibilidadeProfessorMapper.toResponse(disponibilidade);
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
                        Long usuario,
                        Long diaSemana,
                        Long horario,
                        int page, int size) {

                var pageRequest = PageRequest.of(page, size);

                var pageDisponibilidade = repository.buscarComFiltros(
                                usuario,
                                diaSemana,
                                horario,
                                pageRequest);

                return pageDisponibilidade.map(DisponibilidadeProfessorMapper::toResponse);
        }

        @Transactional
        public DisponibilidadeProfessorResponse atualizar(Long id, DisponibilidadeProfessorRequest request) {

                DisponibilidadeProfessor disponibilidade = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Disponibilidade não encontrada com ID: " + id));

                Usuario usuario = usuarioRepository.findById(request.usuario().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Usuário não encontrado com ID: " + request.usuario().id()));

                DiaSemana dia = diaSemanaRepository.findById(request.diaSemana().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Dia da semana não encontrado com ID: " + request.diaSemana().id()));

                Horario horario = horarioRepository.findById(request.horario().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Horário não encontrado com ID: " + request.horario().id()));

                disponibilidade.setUsuario(usuario);
                disponibilidade.setDiaSemana(dia);
                disponibilidade.setHorario(horario);

                repository.save(disponibilidade);

                return DisponibilidadeProfessorMapper.toResponse(disponibilidade);
        }

        @Transactional
        public void deletar(Long id) {
                DisponibilidadeProfessor disponibilidade = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Disponibilidade não encontrada com ID: " + id));

                repository.delete(disponibilidade);
        }
}