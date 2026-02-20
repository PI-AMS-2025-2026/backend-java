package com.fatec.horario.infrastructure.mapper;

import com.fatec.horario.domain.model.CourseUser;
import com.fatec.horario.dto.CourseUser.CourseUserRequest;
import com.fatec.horario.dto.CourseUser.CourseUserResponse;

public class CourseUserMapper {

    public static CourseUser toEntity(CourseUserRequest request) {
        if (request == null) {
            return null;
        }
        CourseUser courseUser = new CourseUser();
        courseUser.setRole(request.role());
        return courseUser;
    }

    public static CourseUserResponse toResponse(CourseUser courseUser) {
        if (courseUser == null) {
            return null;
        }
        return new CourseUserResponse(
                courseUser.getId(),
                courseUser.getUser() != null ? UserMapper.toResponse(courseUser.getUser()) : null,
                courseUser.getCourse() != null ? CourseMapper.toResponse(courseUser.getCourse()) : null,
                courseUser.getRole());
    }
}
