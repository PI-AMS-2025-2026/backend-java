package com.fatec.horario.mappers;

import com.fatec.horario.dtos.ClassGroupRequest;
import com.fatec.horario.dtos.ClassGroupResponse;
import com.fatec.horario.entities.ClassGroup;

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