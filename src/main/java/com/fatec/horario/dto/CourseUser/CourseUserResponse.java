package com.fatec.horario.dto.CourseUser;

import com.fatec.horario.dto.Course.CourseResponse;
import com.fatec.horario.dto.usuario.UserResponse;

public record CourseUserResponse(
                Long id,
                UserResponse user,
                CourseResponse course,
                String role) {

}
