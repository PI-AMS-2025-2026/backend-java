package com.fatec.horario.infrastructure.mapper;

import com.fatec.horario.domain.model.Course;
import com.fatec.horario.dto.CourseRequest;
import com.fatec.horario.dto.CourseResponse;

public class CourseMapper {

    public static Course toEntity(CourseRequest request) {
        Course course = new Course();
        course.setName(request.name());
        course.setDescription(request.description());
        return course;
    }

    public static CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getModality() != null ? ModalityMapper.toResponse(course.getModality()) : null,
                course.getPeriodicity() != null ? PeriodicityMapper.toResponse(course.getPeriodicity()) : null);
    }

}
