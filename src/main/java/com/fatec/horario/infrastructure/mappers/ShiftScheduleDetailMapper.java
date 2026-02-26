package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.ShiftScheduleDetail;
import com.fatec.horario.dto.ShiftScheduleDetail.ShiftScheduleDetailRequest;
import com.fatec.horario.dto.ShiftScheduleDetail.ShiftScheduleDetailResponse;

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
                detail.getEndTime());
    }
}
