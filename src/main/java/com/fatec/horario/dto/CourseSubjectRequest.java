package com.fatec.horario.dto;

public record CourseSubjectRequest(
        Long courseId,
        Long subjectId,
        Integer semesterNumber,
        Integer practicalLessonsCount) {

}
