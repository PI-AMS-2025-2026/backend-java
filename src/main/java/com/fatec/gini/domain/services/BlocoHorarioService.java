package com.fatec.gini.domain.services;

import java.time.Duration;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.services.usecase.read.ValidarBlocoHorarioUseCase;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioRequest;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
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
        LocalDateTime agora = LocalDateTime.now();

        BlocoHorario entity = BlocoHorarioMapper.toEntity(request);
        entity.setDuracao(calcularDuracao(request.horaInicio(), request.horaFim()));
        entity.setCreatedAt(agora);
        entity.setUpdatedAt(agora);
        entity = repository.save(entity);
        return BlocoHorarioMapper.toResponse(entity);
    }

    @Transactional
    public List<BlocoHorarioResponse> criarLote(
            List<BlocoHorarioRequest> requests) {
        LocalDateTime agora = LocalDateTime.now();

        validarBlocoHorarioUseCase.validarDuplicidadesNoLote(requests);

        List<BlocoHorario> entities = requests.stream()
                .peek(validarBlocoHorarioUseCase::executar)
                .map(BlocoHorarioMapper::toEntity)
                .peek(entity -> {
                    entity.setDuracao(calcularDuracao(entity.getHoraInicio(), entity.getHoraFim()));
                    entity.setCreatedAt(agora);
                    entity.setUpdatedAt(agora);
                })
                .toList();

        List<BlocoHorario> salvos = repository.saveAll(entities);

        return salvos.stream()
                .map(BlocoHorarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BlocoHorarioResponse buscarPorId(Long id) {
        BlocoHorario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado com ID: " + id));
        return BlocoHorarioMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public PageResponse<BlocoHorarioResponse> listar(LocalTime horaInicio, LocalTime horaFim, Integer duracao, int pageNum,
            int size) {
        Pageable pageable = PageRequest.of(pageNum, size);

        Page<BlocoHorario> page = repository.buscarPorFiltros(horaInicio, horaFim, duracao, pageable);

       return new PageResponse<>(
            page.getContent().stream().map(BlocoHorarioMapper::toResponse).toList(),
            page.getNumber(),
            page.getSize(),
            page.getNumberOfElements(),
            page.getTotalPages());
    }

    @Transactional
    public BlocoHorarioResponse atualizar(Long id, BlocoHorarioRequest request) {
        validarBlocoHorarioUseCase.executar(request);

        BlocoHorario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado com ID: " + id));

        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setDuracao(calcularDuracao(request.horaInicio(), request.horaFim()));

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

    private int calcularDuracao(LocalTime horaInicio, LocalTime horaFim) {
        return (int) Duration.between(horaInicio, horaFim).toMinutes();
    }

}