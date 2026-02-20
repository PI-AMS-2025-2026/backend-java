package com.fatec.horario.infrastructure.mapper;

import com.fatec.horario.domain.model.AcademicSemester;
import com.fatec.horario.dto.AcademicSemesterRequest;
import com.fatec.horario.dto.AcademicSemesterResponse;

public class AcademicSemesterMapper {

    public static AcademicSemester toEntity(AcademicSemesterRequest request) {
        AcademicSemester academicSemester = new AcademicSemester();
        academicSemester.setAcademicYear(request.academicYear());
        academicSemester.setStatus(request.status());
        return academicSemester;
    }

    public static AcademicSemesterResponse toResponse(AcademicSemester academicSemester) {
        return new AcademicSemesterResponse(
                academicSemester.getId(),
                academicSemester.getAcademicYear(),
                academicSemester.getStatus(),
                academicSemester.getCourse() != null ? CourseMapper.toResponse(academicSemester.getCourse()) : null);
    }

}
