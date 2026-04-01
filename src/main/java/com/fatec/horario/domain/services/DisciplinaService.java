package com.fatec.horario.domain.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.dto.Disciplina.DisciplinaRequest;
import com.fatec.horario.dto.Disciplina.DisciplinaResponse;
import com.fatec.horario.infrastructure.mappers.DisciplinaMapper;
import com.fatec.horario.infrastructure.repositories.DisciplinaRepository;

@Service
public class DisciplinaService {
    
    @Autowired
    private DisciplinaRepository repository;

    @Transactional
    public DisciplinaResponse criar(DisciplinaRequest request) {
        Disciplina entity = DisciplinaMapper.toEntity(request);
        return DisciplinaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public DisciplinaResponse buscarPorId(long id) {
        Disciplina entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));
        return DisciplinaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<DisciplinaResponse> listar(String nome, Long idCurso, Long idTipoSala, Pageable pageable) {
        
        Page<Disciplina> pagina;


        if (nome != null && idCurso != null && idTipoSala != null) {
            pagina = repository.findByNomeContainingIgnoreCaseAndCursoIdCursoAndTipoSalaIdTipoSala(nome, idCurso, idTipoSala, pageable);
        } else if (nome != null) {
            pagina = repository.findByNomeContainingIgnoreCase(nome, pageable);
        } else if (idCurso != null) {
            pagina = repository.findByCursoIdCursoContainingIgnoreCase(idCurso, pageable);
        } else if (idTipoSala != null) {
            pagina = repository.findByTipoSalaIdTipoSalaContainingIgnoreCase(idTipoSala, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }

        return pagina.map(DisciplinaMapper::toResponse);
    }

    @Transactional
    public DisciplinaResponse atualizar(long id, com.fatec.horario.dto.Disciplina.DisciplinaRequest request) {
        Disciplina entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));

                entity.setNome(request.getNome());
                entity.setCargaHoraria(request.getCargaHoraria());
                entity.setTipoDisciplina(request.getTipoDisciplina());
                entity.setPeriodo(request.getPeriodo());
                entity.setModalidade(request.getModalidade());
                entity.setCodDisciplina(request.getCodDisciplina());
                entity.setCor(request.getCor());

                return DisciplinaMapper.toResponse(repository.save(entity));
}

@Transactional
    public void deletar(long id) {
        if(!repository.existsById(id)) {
            throw new EntityNotFoundException("Disciplina não encontrada");
        }
        repository.deleteById(id);
    }
}
