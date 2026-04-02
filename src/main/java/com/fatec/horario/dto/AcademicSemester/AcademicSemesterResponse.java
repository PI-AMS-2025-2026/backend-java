package com.fatec.horario.dto.AcademicSemester;

import com.fatec.horario.dto.curso.CourseResponse;

public record AcademicSemesterResponse(
        Long id,
        Integer academicYear,
        String status,
        CourseResponse course) {
}
