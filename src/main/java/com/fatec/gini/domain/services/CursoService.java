package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.dto.curso.CursoRequest;
import com.fatec.gini.dto.curso.CursoResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.infrastructure.mappers.CursoMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository repository;

    @Transactional
    public CursoResponse criar(CursoRequest dto) {

        // Alteração: normaliza a periodicidade para aceitar
        // maiúsculas e minúsculas.
        String periodicidade = normalizarPeriodicidade(dto.periodicidade());

        // Alteração: valida a duração de acordo com a periodicidade.
        validarDuracao(periodicidade, dto.duracao());

        // Alteração: impede apenas cursos com os três campos iguais.
        if (repository.existsByNomeIgnoreCaseAndPeriodicidadeIgnoreCaseAndDuracao(
                dto.nome(),
                periodicidade,
                dto.duracao())) {

            throw new IllegalArgumentException(
                    "Já existe um curso com o mesmo nome, periodicidade e duração");
        }

        Curso entity = CursoMapper.toEntity(dto);

        // Alteração: salva a periodicidade padronizada.
        entity.setPeriodicidade(periodicidade);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return CursoMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public CursoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(CursoMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public PageResponse<CursoResponse> listar(
            String nome,
            String periodicidade,
            Status status,
            Integer duracao,
            int pageNum,
            int size) {

        var pageRequest = PageRequest.of(pageNum, size);

        var page = repository.buscarPorFiltros(
                nome,
                periodicidade,
                status,
                duracao,
                pageRequest);

        return new PageResponse<>(
                page.getContent().stream()
                        .map(CursoMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public CursoResponse atualizar(Long id, CursoRequest request) {

        Curso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + id));

        // Alteração: normaliza a periodicidade.
        String periodicidade = normalizarPeriodicidade(request.periodicidade());

        // Alteração: valida a duração de acordo com a periodicidade.
        validarDuracao(periodicidade, request.duracao());

        // Alteração: verifica se já existe outro curso
        // com a mesma combinação de nome, periodicidade e duração.
        boolean cursoDuplicado =
                repository.existsByNomeIgnoreCaseAndPeriodicidadeIgnoreCaseAndDuracao(
                        request.nome(),
                        periodicidade,
                        request.duracao());

        // Permite atualizar o próprio curso sem considerá-lo duplicado.
        boolean mesmoCurso =
                entity.getNome().equalsIgnoreCase(request.nome())
                && entity.getPeriodicidade().equalsIgnoreCase(periodicidade)
                && entity.getDuracao().equals(request.duracao());

        if (cursoDuplicado && !mesmoCurso) {
            throw new IllegalArgumentException(
                    "Já existe um curso com o mesmo nome, periodicidade e duração");
        }

        entity.setNome(request.nome());
        entity.setPeriodicidade(periodicidade);
        entity.setStatus(request.status());
        entity.setDuracao(request.duracao());

        entity.setUpdatedAt(LocalDateTime.now());

        return CursoMapper.toResponse(repository.save(entity));
    }

    /**
     * Essa entidade nunca pode ser deletada; em vez disso, ocorre a mudança de
     * status.
     *
     * @param id identificador do curso
     *
     * @throws EntityNotFoundException caso curso não encontrado
     */
    @Transactional
    public void inativar(Long id) {

        Curso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + id));

        entity.setStatus(Status.INATIVO);

        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);
    }

    /**
     * Normaliza a periodicidade para aceitar valores como
     * "anual", "ANUAL", "Anual", "semestral", etc.
     */
    private String normalizarPeriodicidade(String periodicidade) {

        if (periodicidade == null) {
            throw new IllegalArgumentException(
                    "Periodicidade é obrigatória");
        }

        String valor = periodicidade.trim();

        if (valor.equalsIgnoreCase("Anual")) {
            return "Anual";
        }

        if (valor.equalsIgnoreCase("Semestral")) {
            return "Semestral";
        }

        throw new IllegalArgumentException(
                "Periodicidade deve ser 'Semestral' ou 'Anual'");
    }

    /**
     * Valida a duração de acordo com a periodicidade.
     *
     * Anual: duração > 1 e <= 4.
     * Semestral: duração >= 4 e <= 10.
     */
    private void validarDuracao(String periodicidade, Integer duracao) {

        if (duracao == null) {
            throw new IllegalArgumentException(
                    "Duração é obrigatória");
        }

        if (periodicidade.equals("Anual")) {

            if (duracao <= 1 || duracao > 4) {
                throw new IllegalArgumentException(
                        "Para periodicidade Anual, a duração deve ser maior que 1 e menor ou igual a 4");
            }
        }

        if (periodicidade.equals("Semestral")) {

            if (duracao < 4 || duracao > 10) {
                throw new IllegalArgumentException(
                        "Para periodicidade Semestral, a duração deve estar entre 4 e 10");
            }
        }
    }
}