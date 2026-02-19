package com.fatec.horario.dtos;

public record UserAvailabilityResponse(
    Long id,
    Integer weekday,
    Integer lessonNumber,
    UserResponse user
) {

}