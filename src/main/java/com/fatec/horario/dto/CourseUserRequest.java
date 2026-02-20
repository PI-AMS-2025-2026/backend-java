package com.fatec.horario.dto;

import jakarta.validation.constraints.Size;

public record CourseUserRequest(
        Long userId,
        Long courseId,
        @Size(max = 255, message = "Role must be at most 255 characters long") String role) {

}
