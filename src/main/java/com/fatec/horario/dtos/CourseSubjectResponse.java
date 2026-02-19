package com.fatec.horario.dtos;

public record CourseSubjectResponse(
        Long id,
        CourseResponse course,
        SubjectResponse subject,
        Integer semesterNumber,
        Integer practicalLessonsCount) {

}
