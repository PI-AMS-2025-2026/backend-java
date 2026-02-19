package com.fatec.horario.dtos;

public record CourseSubjectRequest(
        Long courseId,
        Long subjectId,
        Integer semesterNumber,
        Integer practicalLessonsCount
    ) {

}
