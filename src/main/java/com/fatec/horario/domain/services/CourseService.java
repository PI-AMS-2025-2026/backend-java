package com.fatec.horario.domain.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.Course;
import com.fatec.horario.domain.entities.Modality;
import com.fatec.horario.domain.entities.Periodicity;
import com.fatec.horario.dto.Course.CourseRequest;
import com.fatec.horario.dto.Course.CourseResponse;
import com.fatec.horario.infrastructure.mappers.CourseMapper;
import com.fatec.horario.infrastructure.repositories.CourseRepository;
import com.fatec.horario.infrastructure.repositories.ModalityRepository;
import com.fatec.horario.infrastructure.repositories.PeriodicityRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CourseService {
    @Autowired
    private CourseRepository repository;

    @Autowired
    private ModalityRepository modalityRepository;

    @Autowired
    private PeriodicityRepository periodicityRepository;

    public List<CourseResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(CourseMapper::toResponse)
                .toList();
    }

    public CourseResponse getById(Long id) {
        Course course = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));

        return CourseMapper.toResponse(course);
    }

    public CourseResponse create(CourseRequest request) {
        Course course = CourseMapper.toEntity(request);

        if (request.modalityId() != null) {
            Modality modality = modalityRepository.findById(request.modalityId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Modality not found with id: " + request.modalityId()));
            course.setModality(modality);
        }

        if (request.periodicityId() != null) {
            Periodicity periodicity = periodicityRepository.findById(request.periodicityId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Periodicity not found with id: " + request.periodicityId()));
            course.setPeriodicity(periodicity);
        }
        course = repository.save(course);
        return CourseMapper.toResponse(course);
    }

    public CourseResponse update(Long id, CourseRequest request) {
        Course course = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
        course.setName(request.name());
        course.setDescription(request.description());

        if (request.modalityId() != null) {
            Modality modality = modalityRepository.findById(request.modalityId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Modality not found with id: " + request.modalityId()));
            course.setModality(modality);
        }

        if (request.periodicityId() != null) {
            Periodicity periodicity = periodicityRepository.findById(request.periodicityId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Periodicity not found with id: " + request.periodicityId()));
            course.setPeriodicity(periodicity);
        }

        course = repository.save(course);
        return CourseMapper.toResponse(course);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Course not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
