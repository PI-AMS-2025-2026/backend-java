package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.AcademicSemester;
import com.fatec.horario.domain.entities.Course;
import com.fatec.horario.dto.AcademicSemester.AcademicSemesterRequest;
import com.fatec.horario.dto.AcademicSemester.AcademicSemesterResponse;
import com.fatec.horario.infrastructure.mappers.AcademicSemesterMapper;
import com.fatec.horario.infrastructure.repositories.AcademicSemesterRepository;
import com.fatec.horario.infrastructure.repositories.CourseRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AcademicSemesterService {

    @Autowired
    private AcademicSemesterRepository academicSemesterRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<AcademicSemesterResponse> getAll() {
        return academicSemesterRepository.findAll()
                .stream()
                .map(AcademicSemesterMapper::toResponse)
                .toList();
    }

    public AcademicSemesterResponse getById(long id) {
        return academicSemesterRepository.findById(id).map(AcademicSemesterMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Academic Semester not found."));
    }

    public AcademicSemesterResponse create(AcademicSemesterRequest request) {
        AcademicSemester academicSemester = AcademicSemesterMapper.toEntity(request);
        academicSemester = academicSemesterRepository.save(academicSemester);
        if (request.courseId() != null) {
            Course course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + request.courseId()));
            academicSemester.setCourse(course);
        }

        return AcademicSemesterMapper.toResponse(academicSemester);
    }

    public AcademicSemesterResponse update(AcademicSemesterRequest request, long id) {
        AcademicSemester academicSemester = academicSemesterRepository.getReferenceById(id);
        academicSemester.setAcademicYear(request.academicYear());
        academicSemester.setStatus(request.status());
        if (request.courseId() != null) {
            Course course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + request.courseId()));
            academicSemester.setCourse(course);
        }
        academicSemesterRepository.save(academicSemester);

        return AcademicSemesterMapper.toResponse(academicSemester);
    }

    public void deleteAcademicSemesterById(long id) {
        if (academicSemesterRepository.existsById(id))
            academicSemesterRepository.deleteById(id);
        else
            throw new EntityNotFoundException("The academic semester does not exist.");

    }

}
