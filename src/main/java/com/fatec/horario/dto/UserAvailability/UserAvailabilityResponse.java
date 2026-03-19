package com.fatec.horario.dto.UserAvailability;

import com.fatec.horario.dto.usuario.UserResponse;

public record UserAvailabilityResponse(
        Long id,
        Integer weekday,
        Integer lessonNumber,
        UserResponse user) {

}
