package com.fatec.horario.dto.CourseSubject;

public record CourseSubjectRequest(
        Long courseId,
        Long subjectId,
        Integer semesterNumber,
        Integer practicalLessonsCount) {

}
