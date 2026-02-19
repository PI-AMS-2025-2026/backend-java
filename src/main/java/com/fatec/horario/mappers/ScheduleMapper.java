package com.fatec.horario.mappers;

import com.fatec.horario.dtos.ScheduleRequest;
import com.fatec.horario.dtos.ScheduleResponse;
import com.fatec.horario.entities.Schedule;

public class ScheduleMapper {

    public static Schedule toEntity(ScheduleRequest request) {
        Schedule schedule = new Schedule();
        schedule.setLessonNumber(request.lessonNumber());
        schedule.setWeekday(request.weekday());
        return schedule;
    }

    public static ScheduleResponse toResponse(Schedule schedule) {
        return new ScheduleResponse(
            schedule.getId(),
            schedule.getLessonNumber(),
            schedule.getWeekday(),
            schedule.getShiftSchedule() != null ? ShiftScheduleMapper.toResponse(schedule.getShiftSchedule()) : null,
            schedule.getClassroom() != null ? ClassroomMapper.toResponse(schedule.getClassroom()) : null,
            schedule.getAcademicSemester() != null ? AcademicSemesterMapper.toResponse(schedule.getAcademicSemester()) : null,
            schedule.getSubject() != null ? SubjectMapper.toResponse(schedule.getSubject()) : null,
            schedule.getProfessor() != null ? UserMapper.toResponse(schedule.getProfessor()) : null
        );
    }
}
