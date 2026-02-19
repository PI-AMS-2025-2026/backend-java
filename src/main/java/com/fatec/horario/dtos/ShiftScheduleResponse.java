package com.fatec.horario.dtos;

public record ShiftScheduleResponse(
        Long id,
        String shiftDescription,
        Integer startTime,
        Integer lessonCount,
        Integer lessonDuration,
        Boolean includedSaturday
) {

}
