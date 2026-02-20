package com.fatec.horario.infrastructure.mapper;

import com.fatec.horario.domain.model.ShiftSchedule;
import com.fatec.horario.dto.ShiftScheduleRequest;
import com.fatec.horario.dto.ShiftScheduleResponse;

public class ShiftScheduleMapper {

    public static ShiftSchedule toEntity(ShiftScheduleRequest request) {

        ShiftSchedule entity = new ShiftSchedule();
        entity.setShiftDescription(request.shiftDescription());
        entity.setStartTime(request.startTime());
        entity.setLessonCount(request.lessonCount());
        entity.setLessonDuration(request.lessonDuration());

        return entity;
    }

    public static ShiftScheduleResponse toResponse(ShiftSchedule entity) {
        ShiftScheduleResponse response = new ShiftScheduleResponse(
                entity.getId(),
                entity.getShiftDescription(),
                entity.getStartTime(),
                entity.getLessonCount(),
                entity.getLessonDuration(),
                entity.getIncludedSaturday());

        return response;
    }
}
