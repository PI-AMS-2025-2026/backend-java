package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.dto.horarios.HorarioRequest;
import com.fatec.horario.dto.horarios.HorarioResponse;
import com.fatec.horario.infrastructure.mappers.HorarioMapper;
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

    public HorarioService(HorarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public HorarioResponse criar(HorarioRequest request) {
        validarHorario(request);

        Horario entity = HorarioMapper.toEntity(request);

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

        Horario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado com ID: " + id));

        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setDuracao(request.duracao());

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
        if (request.horaFim().isBefore(request.horaInicio())) {
            throw new IllegalArgumentException("Hora de fim deve ser posterior à hora de início");
        }
        int duracaoCalculada = request.horaFim().toSecondOfDay() - request.horaInicio().toSecondOfDay();
        duracaoCalculada /= 60; // minutos
        if (duracaoCalculada != request.duracao()) {
            throw new IllegalArgumentException("Duração informada não corresponde ao intervalo entre início e fim");
        }
    }
}