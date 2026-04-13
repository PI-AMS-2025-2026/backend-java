package com.fatec.horario.domain.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.DisponibilidadeProfessor;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.dto.DisponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.horario.dto.DisponibilidadeProfessor.DisponibilidadeProfessorResponse;
import com.fatec.horario.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.horario.infrastructure.repositories.UsuarioRepository;
import com.fatec.horario.infrastructure.repositories.DiaSemanaRepository;
import com.fatec.horario.infrastructure.repositories.HorarioRepository;
import com.fatec.horario.infrastructure.mappers.DisponibilidadeProfessorMapper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DisponibilidadeProfessorService {

    private final DisponibilidadeProfessorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final DiaSemanaRepository diaSemanaRepository;
    private final HorarioRepository horarioRepository;

    public DisponibilidadeProfessorService(
            DisponibilidadeProfessorRepository repository,
            UsuarioRepository usuarioRepository,
            DiaSemanaRepository diaSemanaRepository,
            HorarioRepository horarioRepository) {

        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.diaSemanaRepository = diaSemanaRepository;
        this.horarioRepository = horarioRepository;
    }

    @Transactional
    public DisponibilidadeProfessorResponse criar(DisponibilidadeProfessorRequest request) {
        
        
        if (repository.existsByUsuarioIdAndDiaSemanaIdAndHorarioId(
        request.idUsuario(), request.idDiaSemana(), request.idHorario())) {
            throw new RuntimeException("Disponibilidade já cadastrada para este professor neste dia e horário.");
        }

        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        DiaSemana dia = diaSemanaRepository.findById(request.idDiaSemana())
                .orElseThrow(() -> new EntityNotFoundException("Dia da semana não encontrado"));

        Horario horario = horarioRepository.findById(request.idHorario())
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado"));

        DisponibilidadeProfessor disponibilidade = DisponibilidadeProfessorMapper.toEntity(request, usuario, dia, horario);

        repository.save(disponibilidade);

        return DisponibilidadeProfessorMapper.toResponse(disponibilidade);
    }

    @Transactional(readOnly = true)
    public DisponibilidadeProfessorResponse buscarPorId(Long id) {

        DisponibilidadeProfessor disponibilidade = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disponibilidade não encontrada"));

        return DisponibilidadeProfessorMapper.toResponse(disponibilidade);
    }

    @Transactional(readOnly = true)
    public Page<DisponibilidadeProfessorResponse> listar(
            Long usuario,
            Long diaSemana,
            Long horario,
            Pageable pageable) {

       
        Page<DisponibilidadeProfessor> page = repository.buscarComFiltros(
                usuario,
                diaSemana,
                horario,
                pageable
        );

        return page.map(DisponibilidadeProfessorMapper::toResponse);
    }

    @Transactional
    public DisponibilidadeProfessorResponse atualizar(Long id, DisponibilidadeProfessorRequest request) {

        DisponibilidadeProfessor disponibilidade = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disponibilidade não encontrada"));

        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        DiaSemana dia = diaSemanaRepository.findById(request.idDiaSemana())
                .orElseThrow(() -> new EntityNotFoundException("Dia da semana não encontrado"));

        Horario horario = horarioRepository.findById(request.idHorario())
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado"));

        disponibilidade.setUsuario(usuario);
        disponibilidade.setDiaSemana(dia);
        disponibilidade.setHorario(horario);

        repository.save(disponibilidade);

        return DisponibilidadeProfessorMapper.toResponse(disponibilidade);
    }

    @Transactional
    public void deletar(Long id) {

        DisponibilidadeProfessor disponibilidade = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disponibilidade não encontrada"));

        repository.delete(disponibilidade);
    }
}