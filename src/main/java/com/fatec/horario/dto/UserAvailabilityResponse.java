package com.fatec.horario.dto;

public record UserAvailabilityResponse(
        Long id,
        Integer weekday,
        Integer lessonNumber,
        UserResponse user) {

}
