package com.fatec.horario.dtos;

public record AcademicSemesterResponse (
    Long id,
    Integer academicYear,
    String status,
    CourseResponse course
){}
