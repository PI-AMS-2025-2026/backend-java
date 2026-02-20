package com.fatec.horario.dto;

public record CourseUserResponse(
                Long id,
                UserResponse user,
                CourseResponse course,
                String role) {

}
