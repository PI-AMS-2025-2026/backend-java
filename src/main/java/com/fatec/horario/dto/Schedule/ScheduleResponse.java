package com.fatec.horario.dto.Schedule;

import com.fatec.horario.dto.AcademicSemester.AcademicSemesterResponse;
import com.fatec.horario.dto.Classroom.ClassroomResponse;
import com.fatec.horario.dto.ShiftSchedule.ShiftScheduleResponse;
import com.fatec.horario.dto.Subject.SubjectResponse;
import com.fatec.horario.dto.usuario.UserResponse;

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
