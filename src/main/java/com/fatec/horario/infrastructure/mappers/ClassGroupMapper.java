package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.ClassGroup;
import com.fatec.horario.dto.ClassGroup.ClassGroupRequest;
import com.fatec.horario.dto.ClassGroup.ClassGroupResponse;

public class ClassGroupMapper {

    public static ClassGroup toEntity(ClassGroupRequest request) {
        ClassGroup classGroup = new ClassGroup();
        classGroup.setStudentCount(request.studentCount());
        return classGroup;
    }

    public static ClassGroupResponse toResponse(ClassGroup classGroup) {
        return new ClassGroupResponse(
                classGroup.getId(),
                classGroup.getStudentCount());
    }
}
