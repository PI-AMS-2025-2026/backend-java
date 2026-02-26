package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.Modality;
import com.fatec.horario.domain.entities.Subject;
import com.fatec.horario.domain.entities.TechAxis;
import com.fatec.horario.dto.Subject.SubjectRequest;
import com.fatec.horario.dto.Subject.SubjectResponse;
import com.fatec.horario.infrastructure.mappers.SubjectMapper;
import com.fatec.horario.infrastructure.repositories.CourseSubjectRepository;
import com.fatec.horario.infrastructure.repositories.ModalityRepository;
import com.fatec.horario.infrastructure.repositories.ScheduleRepository;
import com.fatec.horario.infrastructure.repositories.SubjectRepository;
import com.fatec.horario.infrastructure.repositories.TechAxisRepository;
import com.fatec.horario.infrastructure.repositories.UserSubjectRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TechAxisRepository techAxisRepository;

    @Autowired
    private ModalityRepository modalityRepository;

    @Autowired
    private CourseSubjectRepository courseSubjectRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserSubjectRepository userSubjectRepository;

    public List<SubjectResponse> getAll() {
        return subjectRepository.findAll()
                .stream()
                .map(SubjectMapper::toResponse)
                .toList();
    }

    public SubjectResponse getById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));

        return SubjectMapper.toResponse(subject);
    }

    public SubjectResponse create(SubjectRequest request) {
        Subject subject = SubjectMapper.toEntity(request);

        TechAxis techAxis = techAxisRepository.findById(request.techAxisId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "TechAxis not found with id: " + request.techAxisId()));
        subject.setTechAxis(techAxis);

        Modality modality = modalityRepository.findById(request.modalityId())
                .orElseThrow(() -> new EntityNotFoundException("Modality not found with id: " + request.modalityId()));
        subject.setModality(modality);

        subject = subjectRepository.save(subject);
        return SubjectMapper.toResponse(subject);
    }

    public SubjectResponse update(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));

        subject.setName(request.name());
        subject.setAcronym(request.acronym());

        TechAxis techAxis = techAxisRepository.findById(request.techAxisId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "TechAxis not found with id: " + request.techAxisId()));
        subject.setTechAxis(techAxis);

        Modality modality = modalityRepository.findById(request.modalityId())
                .orElseThrow(() -> new EntityNotFoundException("Modality not found with id: " + request.modalityId()));
        subject.setModality(modality);

        subject = subjectRepository.save(subject);
        return SubjectMapper.toResponse(subject);
    }

    @Transactional
    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Subject not found with id: " + id);
        }
        userSubjectRepository.deleteBySubjectId(id);
        courseSubjectRepository.deleteBySubjectId(id);
        scheduleRepository.deleteBySubjectId(id);
        subjectRepository.deleteById(id);
    }
}
