package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.ClassGroup;
import com.fatec.horario.domain.entities.Course;
import com.fatec.horario.dto.ClassGroup.ClassGroupRequest;
import com.fatec.horario.dto.ClassGroup.ClassGroupResponse;

public class ClassGroupMapper {

    public static ClassGroup toEntity(ClassGroupRequest request) {

        ClassGroup classGroup = new ClassGroup();

        // Define quantidade de alunos
        classGroup.setStudentCount(request.studentCount());

        // Cria Course usando o ID recebido
        Course course = new Course();
        course.setId(request.courseId());

        // Vincula a turma ao curso
        classGroup.setCourse(course);

        return classGroup;
    }

    public static ClassGroupResponse toResponse(ClassGroup classGroup) {

        return new ClassGroupResponse(
                classGroup.getId(),
                classGroup.getStudentCount(),
                classGroup.getCourse().getId() // Retorna o ID do curso
        );
    }
}
