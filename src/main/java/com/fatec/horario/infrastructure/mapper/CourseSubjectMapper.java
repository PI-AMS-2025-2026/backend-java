package com.fatec.horario.infrastructure.mapper;

import com.fatec.horario.domain.model.CourseSubject;
import com.fatec.horario.dto.CourseSubjectRequest;
import com.fatec.horario.dto.CourseSubjectResponse;

public class CourseSubjectMapper {

    public static CourseSubject toEntity(CourseSubjectRequest request) {
        if (request == null) {
            return null;
        }
        CourseSubject courseSubject = new CourseSubject();
        courseSubject.setSemesterNumber(request.semesterNumber());
        courseSubject.setPracticalLessonsCount(request.practicalLessonsCount());
        return courseSubject;
    }

    public static CourseSubjectResponse toResponse(CourseSubject courseSubject) {
        if (courseSubject == null) {
            return null;
        }
        return new CourseSubjectResponse(
                courseSubject.getId(),
                courseSubject.getCourse() != null ? CourseMapper.toResponse(courseSubject.getCourse()) : null,
                courseSubject.getSubject() != null ? SubjectMapper.toResponse(courseSubject.getSubject()) : null,
                courseSubject.getSemesterNumber(),
                courseSubject.getPracticalLessonsCount());
    }
}
