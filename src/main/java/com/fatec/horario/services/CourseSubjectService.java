package com.fatec.horario.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.dtos.CourseSubjectRequest;
import com.fatec.horario.dtos.CourseSubjectResponse;
import com.fatec.horario.entities.Course;
import com.fatec.horario.entities.CourseSubject;
import com.fatec.horario.entities.Subject;
import com.fatec.horario.mappers.CourseSubjectMapper;
import com.fatec.horario.repositories.CourseRepository;
import com.fatec.horario.repositories.CourseSubjectRepository;
import com.fatec.horario.repositories.SubjectRepository;

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
                    .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + request.subjectId()));
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
                    .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + request.subjectId()));
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
