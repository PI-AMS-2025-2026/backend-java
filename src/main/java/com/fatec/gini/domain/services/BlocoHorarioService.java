package com.fatec.gini.domain.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.services.usecase.read.ValidarBlocoHorarioUseCase;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioRequest;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioResponse;
import com.fatec.gini.infrastructure.mappers.BlocoHorarioMapper;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlocoHorarioService {

    private final BlocoHorarioRepository repository;
    private final ValidarBlocoHorarioUseCase validarBlocoHorarioUseCase;

    @Transactional
    public BlocoHorarioResponse criar(BlocoHorarioRequest request) {
        validarBlocoHorarioUseCase.executar(request);

        BlocoHorario entity = BlocoHorarioMapper.toEntity(request);
        entity.setDuracao(calcularDuracao(request));
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
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
    public Page<BlocoHorarioResponse> listar(LocalTime horaInicio, LocalTime horaFim, Integer duracao, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<BlocoHorario> pageBlocoHorario = repository.buscarPorFiltros(horaInicio, horaFim, duracao, pageable);

        return pageBlocoHorario.map(BlocoHorarioMapper::toResponse);
    }

    @Transactional
    public BlocoHorarioResponse atualizar(Long id, BlocoHorarioRequest request) {
        validarBlocoHorarioUseCase.executar(request);

        BlocoHorario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado com ID: " + id));

        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setDuracao(calcularDuracao(request));

        entity.setUpdatedAt(LocalDateTime.now());
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

    private int calcularDuracao(BlocoHorarioRequest request) {
        return (int) Duration.between(request.horaInicio(), request.horaFim()).toMinutes();
    }

}