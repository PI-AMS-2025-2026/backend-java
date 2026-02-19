package com.fatec.horario.mappers;

import com.fatec.horario.dtos.ShiftScheduleDetailRequest;
import com.fatec.horario.dtos.ShiftScheduleDetailResponse;
import com.fatec.horario.entities.ShiftScheduleDetail;

public class ShiftScheduleDetailMapper {

    public static ShiftScheduleDetail toEntity(ShiftScheduleDetailRequest request) {
        if (request == null) {
            return null;
        }
        ShiftScheduleDetail detail = new ShiftScheduleDetail();
        detail.setLessonNumber(request.lessonNumber());
        detail.setStartTime(request.startTime());
        detail.setEndTime(request.endTime());
        return detail;
    }

    public static ShiftScheduleDetailResponse toResponse(ShiftScheduleDetail detail) {
        if (detail == null) {
            return null;
        }
        return new ShiftScheduleDetailResponse(
            detail.getId(),
            detail.getShiftSchedule() != null ? ShiftScheduleMapper.toResponse(detail.getShiftSchedule()) : null,
            detail.getLessonNumber(),
            detail.getStartTime(),
            detail.getEndTime()
        );
    }
}
