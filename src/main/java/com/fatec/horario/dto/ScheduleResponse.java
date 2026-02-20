package com.fatec.horario.dto;

public record ScheduleResponse(
        Long id,
        Integer lessonNumber,
        Integer weekday,
        ShiftScheduleResponse shiftSchedule,
        ClassroomResponse classroom,
        AcademicSemesterResponse academicSemester,
        SubjectResponse subject,
        UserResponse professor) {
}
