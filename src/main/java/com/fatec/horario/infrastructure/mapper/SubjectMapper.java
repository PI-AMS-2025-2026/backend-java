package com.fatec.horario.infrastructure.mapper;

import com.fatec.horario.domain.model.Subject;
import com.fatec.horario.dto.Subject.SubjectRequest;
import com.fatec.horario.dto.Subject.SubjectResponse;

import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {

    public static Subject toEntity(SubjectRequest request) {

        Subject subject = new Subject();
        subject.setName(request.name());
        subject.setAcronym(request.acronym());

        return subject;
    }

    public static SubjectResponse toResponse(Subject subject) {
        return new SubjectResponse(
                subject.getId(),
                subject.getName(),
                subject.getAcronym(),
                subject.getTechAxis() != null ? TechAxisMapper.toResponse(subject.getTechAxis()) : null,
                subject.getModality() != null ? ModalityMapper.toResponse(subject.getModality()) : null);
    }
}
