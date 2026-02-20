package com.fatec.horario.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.model.Classroom;
import com.fatec.horario.dto.ClassroomRequest;
import com.fatec.horario.dto.ClassroomResponse;
import com.fatec.horario.infrastructure.mapper.ClassroomMapper;
import com.fatec.horario.infrastructure.repository.ClassroomRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository repository;

    public List<ClassroomResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(ClassroomMapper::toResponse)
                .toList();
    }

    public ClassroomResponse getById(long id) {
        return repository.findById(id)
                .map(ClassroomMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found."));
    }

    public ClassroomResponse save(ClassroomRequest request) {
        Classroom classroom = ClassroomMapper.toEntity(request);
        classroom = repository.save(classroom);
        return ClassroomMapper.toResponse(classroom);
    }

    public ClassroomResponse update(Long id, ClassroomRequest request) {
        Classroom classroom = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found."));

        classroom.setName(request.name());
        classroom.setLocation(request.location());
        classroom.setPhysicalResources(request.physicalResources());
        classroom.setSoftwareResources(request.softwareResources());
        classroom.setCapacity(request.capacity());
        classroom.setTemplate(request.template());
        classroom.setPractical(request.practical());

        classroom = repository.save(classroom);

        return ClassroomMapper.toResponse(classroom);
    }

    public void delete(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Classroom not found.");
        }
    }

    public List<ClassroomResponse> getAllTemplates() {
        return repository.findByTemplate(true)
                .stream()
                .map(ClassroomMapper::toResponse)
                .toList();
    }

    public List<ClassroomResponse> getAllClassrooms() {
        return repository.findByTemplate(false)
                .stream()
                .map(ClassroomMapper::toResponse)
                .toList();
    }
}
