package com.fatec.horario.dtos;

public record ScheduleResponse (
    Long id,
    Integer lessonNumber,
    Integer weekday,
    ShiftScheduleResponse shiftSchedule,
    ClassroomResponse classroom,
    AcademicSemesterResponse academicSemester,
    SubjectResponse subject,
    UserResponse professor
){}
