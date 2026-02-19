package com.fatec.horario.dtos;

public record CourseUserResponse(
        Long id,
        UserResponse user,
        CourseResponse course,
        String role) {

}
