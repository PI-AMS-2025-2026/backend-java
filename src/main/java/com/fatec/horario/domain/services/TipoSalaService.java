package com.fatec.horario.domain.services;

import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.TipoSala;
import com.fatec.horario.infrastructure.repositories.TipoSalaRepository;

import java.util.List;

@Service
public class TipoSalaService {

    private final TipoSalaRepository repository;

    
    public TipoSalaService(TipoSalaRepository repository) {
        this.repository = repository;
    }

        public TipoSala criar(TipoSala tipoSala) {
        return repository.save(tipoSala);
    }

        public List<TipoSala> listar() {
        return repository.findAll();
    }

        public TipoSala buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

        public TipoSala atualizar(Long id, TipoSala tipoSala) {
        TipoSala existente = buscarPorId(id);
        existente.setNome(tipoSala.getNome());
        return repository.save(existente);
    }

        public void deletar(Long id) {
        repository.deleteById(id);
    }
    
}