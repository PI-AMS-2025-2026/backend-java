package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.PeriodoLetivo;
import com.fatec.horario.dto.PeriodoLetivo.PeriodoLetivoRequest;
import com.fatec.horario.dto.PeriodoLetivo.PeriodoLetivoResponse;
import com.fatec.horario.infrastructure.mappers.PeriodoLetivoMapper;
import com.fatec.horario.infrastructure.repositories.PeriodoLetivoRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PeriodoLetivoService {

    @Autowired
    private PeriodoLetivoRepository repository;

    @Transactional
    public PeriodoLetivoResponse criar(PeriodoLetivoRequest request) {
        PeriodoLetivo entity = PeriodoLetivoMapper.toEntity(request);
        return PeriodoLetivoMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public PeriodoLetivoResponse buscarPorId(long id) {
        PeriodoLetivo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Periodo letivo não encontrado"));
        return PeriodoLetivoMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<PeriodoLetivoResponse> listar() {
        return repository.findAll()
                .stream()
                .map(PeriodoLetivoMapper::toResponse)
                .toList();
    }

    @Transactional
    public PeriodoLetivoResponse atualizar(long id, PeriodoLetivoRequest request) {
        PeriodoLetivo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Periodo letivo não encontrado"));

        entity.setAno(request.ano());
        entity.setPeriodo(request.periodo());
        entity.setDataInicio(request.dataInicio());
        entity.setDataFim(request.dataFim());
        entity.setStatus(request.status());

        return PeriodoLetivoMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Periodo letivo não existe");
        }
        repository.deleteById(id);
    }

}
