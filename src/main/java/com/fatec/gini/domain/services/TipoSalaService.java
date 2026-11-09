package com.fatec.gini.domain.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.TipoSala;
import com.fatec.gini.dto.tipoSala.TipoSalaRequest;
import com.fatec.gini.dto.tipoSala.TipoSalaResponse;
import com.fatec.gini.infrastructure.mappers.TipoSalaMapper;
import com.fatec.gini.infrastructure.repositories.TipoSalaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityManager;

@Service
@RequiredArgsConstructor
public class TipoSalaService {

    private final TipoSalaRepository repository;
    
// O EntityManager permite executar consultas diretamente no banco de dados
// por meio do JPA. Neste serviço, será utilizado para verificar se existem
// salas ou disciplinas vinculadas a um tipo de sala antes de permitir sua exclusão.
   private final EntityManager entityManager;

    // Lista todos ou filtra por nome
    @Transactional(readOnly = true)
    public List<TipoSalaResponse> listar(String nome) {

        List<TipoSala> lista = repository.buscarPorFiltro(nome);

        return lista.stream()
                .map(TipoSalaMapper::toResponse)
                .toList();
    }

    // Cria um novo tipo de sala
    @Transactional
    public TipoSalaResponse criar(TipoSalaRequest request) {

        if (repository.existsByNomeIgnoreCase(request.nome())) {
    throw new IllegalArgumentException("Esse tipo_sala já possui cadastro");
    }

        TipoSala entity = TipoSalaMapper.toEntity(request);

        return TipoSalaMapper.toResponse(repository.save(entity));
    }

    // Busca por ID com tratamento de erro
    @Transactional(readOnly = true)
    public TipoSalaResponse buscarPorId(Long id) {
        TipoSala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de sala não encontrado com ID: " + id));
        return TipoSalaMapper.toResponse(entity);
    }

    // Atualiza um tipo de sala existente
    @Transactional
    public TipoSalaResponse atualizar(Long id, TipoSalaRequest request) {
        TipoSala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de sala não encontrado com ID: " + id));

        if (repository.existsByNomeIgnoreCaseAndIdNot(request.nome(), id)) {
    throw new IllegalArgumentException(
        "Já existe outro tipo de sala cadastrado com esse nome.");
    }

        entity.setNome(request.nome());

        return TipoSalaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("tipo_sala não encontrado com ID: " + id);
        }

    // Verifica se existem salas vinculadas ao tipo de sala.
    Long salasVinculadas = entityManager.createQuery(
        "SELECT COUNT(s) FROM Sala s WHERE s.tipoSala.id = :id", Long.class
    ).setParameter("id", id).getSingleResult();
 
    if (salasVinculadas > 0) {
        throw new IllegalArgumentException("Esse tipo_sala está vinculado a uma sala.");
    }
 
    // Verifica se existem disciplinas vinculadas ao tipo de sala.
    Long disciplinasVinculadas = entityManager.createQuery(
        "SELECT COUNT(d) FROM Disciplina d WHERE d.tipoSala.id = :id", Long.class
    ).setParameter("id", id).getSingleResult();
 
    if (disciplinasVinculadas > 0) {
        throw new IllegalArgumentException("Esse tipo_sala está vinculado a uma disciplina.");
    }
 
    repository.deleteById(id);
    }
}