package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import para controle de transação

import com.fatec.horario.domain.entities.ClassGroup;
import com.fatec.horario.dto.ClassGroup.ClassGroupRequest;
import com.fatec.horario.dto.ClassGroup.ClassGroupResponse;
import com.fatec.horario.infrastructure.mappers.ClassGroupMapper;
import com.fatec.horario.infrastructure.repositories.ClassGroupRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClassGroupService {

    @Autowired
    private ClassGroupRepository repository;

    // Método apenas de leitura
    @Transactional(readOnly = true)
    public List<ClassGroupResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(ClassGroupMapper::toResponse)
                .toList();
    }

    // Busca por ID (somente leitura)
    @Transactional(readOnly = true)
    public ClassGroupResponse getById(long id) {
        return repository.findById(id)
                .map(ClassGroupMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Class group not registered"));
    }

    // Remove uma turma
    @Transactional
    public void delete(long id) {
        if (repository.existsById(id))
            repository.deleteById(id);
        else
            throw new EntityNotFoundException("Class group not exist");
    }

    // Cria uma nova turma
    @Transactional
    public ClassGroupResponse create(ClassGroupRequest request) {

        // Converte DTO para entidade
        ClassGroup classGroup = ClassGroupMapper.toEntity(request);

        // Salva no banco
        ClassGroup savedClassGroup = repository.save(classGroup);

        // Retorna DTO de resposta
        return ClassGroupMapper.toResponse(savedClassGroup);
    }

    // Atualiza uma turma existente
    @Transactional
    public ClassGroupResponse update(ClassGroupRequest request, long id) {

        // Busca a turma pelo ID
        ClassGroup classGroup = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class group not found"));

        // Atualiza os dados
        classGroup.setStudentCount(request.studentCount());

        // Salva alterações
        classGroup = repository.save(classGroup);

        return ClassGroupMapper.toResponse(classGroup);
    }

}
