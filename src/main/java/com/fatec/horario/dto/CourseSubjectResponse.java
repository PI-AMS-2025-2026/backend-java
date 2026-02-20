package com.fatec.horario.dto;

public record CourseSubjectResponse(
                Long id,
                CourseResponse course,
                SubjectResponse subject,
                Integer semesterNumber,
                Integer practicalLessonsCount) {

}
