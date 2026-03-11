package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Course;
import com.fatec.horario.dto.Course.CourseRequest;
import com.fatec.horario.dto.Course.CourseResponse;
import com.fatec.horario.infrastructure.mappers.CourseMapper;
import com.fatec.horario.infrastructure.repositories.CourseRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CourseService {

    @Autowired
    private CourseRepository repository;

    @Transactional(readOnly = true)
    public List<CourseResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(CourseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseResponse getById(Long id) {
        Course course = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));

        return CourseMapper.toResponse(course);
    }

    @Transactional
    public CourseResponse create(CourseRequest request) {
        Course course = CourseMapper.toEntity(request);

        course = repository.save(course);

        return CourseMapper.toResponse(course);
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {

        Course course = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));

        course.setName(request.name());
        course.setDescription(request.description());

        course = repository.save(course);

        return CourseMapper.toResponse(course);
    }

    @Transactional
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Course not found with id: " + id);
        }

        repository.deleteById(id);
    }
}