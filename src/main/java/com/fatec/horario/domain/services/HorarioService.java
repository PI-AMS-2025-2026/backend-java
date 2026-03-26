package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.AccessLevel;
import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.dto.Horarios.HorarioRequest;
import com.fatec.horario.dto.Horarios.HorarioResponse;
import com.fatec.horario.infrastructure.mappers.HorarioMapper;
import com.fatec.horario.infrastructure.repositories.AccessLevelRepository;
import com.fatec.horario.infrastructure.repositories.HorarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
public class HorarioService {

    private final HorarioRepository repository;
    private final AccessLevelRepository accessLevelRepository;

    public HorarioService(HorarioRepository repository, AccessLevelRepository accessLevelRepository) {
        this.repository = repository;
        this.accessLevelRepository = accessLevelRepository;
    }

    @Transactional
    public HorarioResponse criar(HorarioRequest request) {
        validarHorario(request);

        AccessLevel accessLevel = accessLevelRepository.findById(request.getAccessLevelId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de usuário não encontrado com ID: " + request.getAccessLevelId()));

        Horario entity = HorarioMapper.toEntity(request, accessLevel);

        entity = repository.save(entity);
        return HorarioMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public HorarioResponse buscarPorId(Long id) {
        Horario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado com ID: " + id));
        return HorarioMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<HorarioResponse> listar(LocalTime horaInicio, LocalTime horaFim, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Horario> result;
        if (horaInicio != null && horaFim != null) {
            result = repository.findByHoraInicioAndHoraFim(horaInicio, horaFim, pageable);
        } else if (horaInicio != null) {
            result = repository.findByHoraInicio(horaInicio, pageable);
        } else if (horaFim != null) {
            result = repository.findByHoraFim(horaFim, pageable);
        } else {
            result = repository.findAll(pageable);
        }

        return result.map(HorarioMapper::toResponse);
    }

    @Transactional
    public HorarioResponse atualizar(Long id, HorarioRequest request) {
        validarHorario(request);

        AccessLevel accessLevel = accessLevelRepository.findById(request.getAccessLevelId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de usuário não encontrado com ID: " + request.getAccessLevelId()));

        Horario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado com ID: " + id));

        entity.setHoraInicio(request.getHoraInicio());
        entity.setHoraFim(request.getHoraFim());
        entity.setDuracao(request.getDuracao());
        entity.setAccessLevel(accessLevel);

        entity = repository.save(entity);
        return HorarioMapper.toResponse(entity);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Horário não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }

    private void validarHorario(HorarioRequest request) {
        if (request.getHoraFim().isBefore(request.getHoraInicio())) {
            throw new IllegalArgumentException("Hora de fim deve ser posterior à hora de início");
        }
        int duracaoCalculada = request.getHoraFim().toSecondOfDay() - request.getHoraInicio().toSecondOfDay();
        duracaoCalculada /= 60; // minutos
        if (duracaoCalculada != request.getDuracao()) {
            throw new IllegalArgumentException("Duração informada não corresponde ao intervalo entre início e fim");
        }
    }
}