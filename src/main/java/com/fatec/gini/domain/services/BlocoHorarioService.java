package com.fatec.gini.domain.services;

import java.time.Duration;
import java.time.LocalTime;
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
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.infrastructure.repositories.HistoricoVersaoAlocacaoRepository;
import com.fatec.gini.web.exception.BlocoHorarioComVinculosException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlocoHorarioService {

    private final BlocoHorarioRepository repository;
    private final ValidarBlocoHorarioUseCase validarBlocoHorarioUseCase;
    private final AlocacaoRepository alocacaoRepository;
    private final DisponibilidadeProfessorRepository disponibilidadeRepository;
    private final HistoricoVersaoAlocacaoRepository historicoRepository;

    @Transactional
    public BlocoHorarioResponse criar(BlocoHorarioRequest request) {
        validarBlocoHorarioUseCase.executar(request);
        BlocoHorario entity = BlocoHorarioMapper.toEntity(request);
        entity.setDuracao(calcularDuracao(request.horaInicio(), request.horaFim()));
        entity = repository.save(entity);
        return BlocoHorarioMapper.toResponse(entity);
    }

    @Transactional
    public List<BlocoHorarioResponse> criarLote(
            List<BlocoHorarioRequest> requests) {
        validarBlocoHorarioUseCase.validarDuplicidadesNoLote(requests);

        List<BlocoHorario> entities = requests.stream()
                .peek(validarBlocoHorarioUseCase::executar)
                .map(BlocoHorarioMapper::toEntity)
                .peek(entity -> {
                    entity.setDuracao(calcularDuracao(entity.getHoraInicio(), entity.getHoraFim()));
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
                .orElseThrow(() -> new EntityNotFoundException("Bloco de horário não encontrado com ID: " + id));
        return BlocoHorarioMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public PageResponse<BlocoHorarioResponse> listar(LocalTime horaInicio, LocalTime horaFim, Integer duracao, String busca, int pageNum,
            int size) {
        Pageable pageable = PageRequest.of(pageNum, size);

        Page<BlocoHorario> page = repository.buscarPorFiltros(horaInicio, horaFim, duracao, busca, pageable);

       return new PageResponse<>(
            page.getContent().stream().map(BlocoHorarioMapper::toResponse).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages());
    }

    @Transactional
    public BlocoHorarioResponse atualizar(Long id, BlocoHorarioRequest request) {
        validarBlocoHorarioUseCase.executar(request);

        BlocoHorario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bloco de horário não encontrado com ID: " + id));

        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setDuracao(calcularDuracao(request.horaInicio(), request.horaFim()));

        entity = repository.save(entity);
        return BlocoHorarioMapper.toResponse(entity);
    }

    @Transactional
    public void deletar(Long id, boolean confirmarVinculos) {
        BlocoHorario bloco = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bloco de horário não encontrado com ID: " + id));
        var alocacoes = alocacaoRepository.findByBlocoHorarioId(id);
        var disponibilidades = disponibilidadeRepository.findByBlocoHorarioId(id);
        if ((!alocacoes.isEmpty() || !disponibilidades.isEmpty()) && !confirmarVinculos) {
            throw new BlocoHorarioComVinculosException(alocacoes.size(), disponibilidades.size());
        }
        if (!alocacoes.isEmpty()) {
            historicoRepository.desvincularAlocacoes(alocacoes.stream().map(a -> a.getId()).toList());
            alocacaoRepository.deleteAll(alocacoes);
        }
        disponibilidadeRepository.deleteAll(disponibilidades);
        repository.delete(bloco);
    }

    private int calcularDuracao(LocalTime horaInicio, LocalTime horaFim) {
        return (int) Duration.between(horaInicio, horaFim).toMinutes();
    }

}
