package com.fatec.gini.domain.services;

import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioRequest;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioResponse;
import com.fatec.gini.infrastructure.mappers.BlocoHorarioMapper;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlocoHorarioService {

    private final  BlocoHorarioRepository repository;

    @Transactional
    public BlocoHorarioResponse criar(BlocoHorarioRequest request) {
        validarBlocoHorario(request);

        BlocoHorario entity = BlocoHorarioMapper.toEntity(request);

        entity = repository.save(entity);
        return BlocoHorarioMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public BlocoHorarioResponse buscarPorId(Long id) {
        BlocoHorario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado com ID: " + id));
        return BlocoHorarioMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<BlocoHorarioResponse> listar(LocalTime horaInicio, LocalTime horaFim, Integer duracao, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<BlocoHorario> pageBlocoHorario = repository.buscarPorFiltros(horaInicio, horaFim, duracao, pageable);

        return pageBlocoHorario.map(BlocoHorarioMapper::toResponse);
    }

    @Transactional
    public BlocoHorarioResponse atualizar(Long id, BlocoHorarioRequest request) {
        validarBlocoHorario(request);

        BlocoHorario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado com ID: " + id));

        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setDuracao(request.duracao());

        entity = repository.save(entity);
        return BlocoHorarioMapper.toResponse(entity);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Horário não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }

    // TODO: colocar a validação do local correto
    private void validarBlocoHorario(BlocoHorarioRequest request) {
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