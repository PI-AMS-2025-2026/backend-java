package com.fatec.horario.dto;

public record AcademicSemesterResponse(
        Long id,
        Integer academicYear,
        String status,
        CourseResponse course) {
}
