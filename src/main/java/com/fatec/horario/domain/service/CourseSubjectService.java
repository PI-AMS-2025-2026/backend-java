package com.fatec.horario.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.model.Course;
import com.fatec.horario.domain.model.CourseSubject;
import com.fatec.horario.domain.model.Subject;
import com.fatec.horario.dto.CourseSubject.CourseSubjectRequest;
import com.fatec.horario.dto.CourseSubject.CourseSubjectResponse;
import com.fatec.horario.infrastructure.mapper.CourseSubjectMapper;
import com.fatec.horario.infrastructure.repository.CourseRepository;
import com.fatec.horario.infrastructure.repository.CourseSubjectRepository;
import com.fatec.horario.infrastructure.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CourseSubjectService {

    @Autowired
    private CourseSubjectRepository courseSubjectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    public List<CourseSubjectResponse> getAll() {
        return courseSubjectRepository.findAll()
                .stream()
                .map(CourseSubjectMapper::toResponse)
                .toList();
    }

    public CourseSubjectResponse getById(Long id) {
        CourseSubject courseSubject = courseSubjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CourseSubject not found with id: " + id));
        return CourseSubjectMapper.toResponse(courseSubject);
    }

    public CourseSubjectResponse create(CourseSubjectRequest request) {
        CourseSubject courseSubject = CourseSubjectMapper.toEntity(request);

        if (request.courseId() != null) {
            Course course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + request.courseId()));
            courseSubject.setCourse(course);
        }

        if (request.subjectId() != null) {
            Subject subject = subjectRepository.findById(request.subjectId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Subject not found with id: " + request.subjectId()));
            courseSubject.setSubject(subject);
        }

        courseSubject = courseSubjectRepository.save(courseSubject);
        return CourseSubjectMapper.toResponse(courseSubject);
    }

    public CourseSubjectResponse update(Long id, CourseSubjectRequest request) {
        CourseSubject courseSubject = courseSubjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CourseSubject not found with id: " + id));

        courseSubject.setSemesterNumber(request.semesterNumber());
        courseSubject.setPracticalLessonsCount(request.practicalLessonsCount());

        if (request.courseId() != null) {
            Course course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + request.courseId()));
            courseSubject.setCourse(course);
        }

        if (request.subjectId() != null) {
            Subject subject = subjectRepository.findById(request.subjectId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Subject not found with id: " + request.subjectId()));
            courseSubject.setSubject(subject);
        }

        courseSubject = courseSubjectRepository.save(courseSubject);
        return CourseSubjectMapper.toResponse(courseSubject);
    }

    public void delete(Long id) {
        if (!courseSubjectRepository.existsById(id)) {
            throw new EntityNotFoundException("CourseSubject not found with id: " + id);
        }
        courseSubjectRepository.deleteById(id);
    }
}
