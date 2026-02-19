package com.fatec.horario.mappers;

import com.fatec.horario.dtos.CourseSubjectRequest;
import com.fatec.horario.dtos.CourseSubjectResponse;
import com.fatec.horario.entities.CourseSubject;

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
            courseSubject.getPracticalLessonsCount()
        );
    }
}
