package com.fatec.horario.dto.CourseSubject;

import com.fatec.horario.dto.Course.CourseResponse;
import com.fatec.horario.dto.Subject.SubjectResponse;

public record CourseSubjectResponse(
                Long id,
                CourseResponse course,
                SubjectResponse subject,
                Integer semesterNumber,
                Integer practicalLessonsCount) {

}
