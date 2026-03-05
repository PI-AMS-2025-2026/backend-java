package com.fatec.horario.dto.ClassGroup;

public record ClassGroupResponse(

      Long id, // ID da turma

      Integer studentCount, // Quantidade de alunos

      Long courseId // ID do curso vinculado

) {
}
