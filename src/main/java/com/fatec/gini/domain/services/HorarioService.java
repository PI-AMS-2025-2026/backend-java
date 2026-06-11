package com.fatec.gini.domain.services;

import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Horario;
import com.fatec.gini.dto.horarios.HorarioRequest;
import com.fatec.gini.dto.horarios.HorarioResponse;
import com.fatec.gini.infrastructure.mappers.HorarioMapper;
import com.fatec.gini.infrastructure.repositories.HorarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HorarioService {

    private final  HorarioRepository repository;

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
    public Page<HorarioResponse> listar(LocalTime horaInicio, LocalTime horaFim, Integer duracao, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Horario> pageHorario = repository.buscarPorFiltros(horaInicio, horaFim, duracao, pageable);

        return pageHorario.map(HorarioMapper::toResponse);
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

    // TODO: colocar a validação do local correto
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