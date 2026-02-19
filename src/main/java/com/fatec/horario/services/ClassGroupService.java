package com.fatec.horario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.dtos.ClassGroupRequest;
import com.fatec.horario.dtos.ClassGroupResponse;
import com.fatec.horario.entities.ClassGroup;
import com.fatec.horario.mappers.ClassGroupMapper;
import com.fatec.horario.repositories.ClassGroupRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClassGroupService {

    @Autowired
    private ClassGroupRepository repository;

    public List<ClassGroupResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(ClassGroupMapper::toResponse)
                .toList();
    }

    public ClassGroupResponse getById(long id) {
        return repository.findById(id)
                .map(ClassGroupMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Class group not registered"));
    }

    public void delete(long id) {
        if (repository.existsById(id))
            repository.deleteById(id);
        else
            throw new EntityNotFoundException("Class group not exist");

    }

    public ClassGroupResponse create(ClassGroupRequest request) {
        ClassGroup ClassGroup = ClassGroupMapper.toEntity(request);

        ClassGroup savedClassGroup = repository.save(ClassGroup);
        return ClassGroupMapper.toResponse(savedClassGroup);
    }

    public ClassGroupResponse update(ClassGroupRequest request, long id) {
        ClassGroup classGroup = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class group not found"));

        classGroup.setStudentCount(request.studentCount());
        classGroup = repository.save(classGroup);
        return ClassGroupMapper.toResponse(classGroup);
    }

}
