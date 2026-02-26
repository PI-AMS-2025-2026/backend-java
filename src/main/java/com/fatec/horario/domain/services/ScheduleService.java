package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.Schedule;
import com.fatec.horario.dto.Schedule.ScheduleRequest;
import com.fatec.horario.dto.Schedule.ScheduleResponse;
import com.fatec.horario.infrastructure.mappers.ScheduleMapper;
import com.fatec.horario.infrastructure.repositories.AcademicSemesterRepository;
import com.fatec.horario.infrastructure.repositories.ClassroomRepository;
import com.fatec.horario.infrastructure.repositories.ScheduleRepository;
import com.fatec.horario.infrastructure.repositories.ShiftScheduleRepository;
import com.fatec.horario.infrastructure.repositories.SubjectRepository;
import com.fatec.horario.infrastructure.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository repository;

    @Autowired
    private ShiftScheduleRepository shiftScheduleRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private AcademicSemesterRepository academicSemesterRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ScheduleResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(ScheduleMapper::toResponse)
                .toList();
    }

    public ScheduleResponse getById(Long id) {
        Schedule schedule = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id: " + id));
        return ScheduleMapper.toResponse(schedule);
    }

    public ScheduleResponse create(ScheduleRequest request) {
        Schedule schedule = ScheduleMapper.toEntity(request);
        if (request.shiftScheduleId() != null) {
            var s = shiftScheduleRepository.findById(request.shiftScheduleId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "ShiftSchedule not found with id: " + request.shiftScheduleId()));
            schedule.setShiftSchedule(s);
        }
        if (request.classroomId() != null) {
            var c = classroomRepository.findById(request.classroomId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Classroom not found with id: " + request.classroomId()));
            schedule.setClassroom(c);
        }
        if (request.academicSemesterId() != null) {
            var a = academicSemesterRepository.findById(request.academicSemesterId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "AcademicSemester not found with id: " + request.academicSemesterId()));
            schedule.setAcademicSemester(a);
        }
        if (request.subjectId() != null) {
            var subj = subjectRepository.findById(request.subjectId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Subject not found with id: " + request.subjectId()));
            schedule.setSubject(subj);
        }
        if (request.professorId() != null) {
            var u = userRepository.findById(request.professorId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.professorId()));
            schedule.setProfessor(u);
        }
        schedule = repository.save(schedule);
        return ScheduleMapper.toResponse(schedule);
    }

    public ScheduleResponse update(Long id, ScheduleRequest request) {
        Schedule schedule = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id: " + id));

        schedule.setLessonNumber(request.lessonNumber());
        schedule.setWeekday(request.weekday());
        if (request.shiftScheduleId() != null) {
            var s = shiftScheduleRepository.findById(request.shiftScheduleId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "ShiftSchedule not found with id: " + request.shiftScheduleId()));
            schedule.setShiftSchedule(s);
        } else {
            schedule.setShiftSchedule(null);
        }
        if (request.classroomId() != null) {
            var c = classroomRepository.findById(request.classroomId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Classroom not found with id: " + request.classroomId()));
            schedule.setClassroom(c);
        } else {
            schedule.setClassroom(null);
        }
        if (request.academicSemesterId() != null) {
            var a = academicSemesterRepository.findById(request.academicSemesterId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "AcademicSemester not found with id: " + request.academicSemesterId()));
            schedule.setAcademicSemester(a);
        } else {
            schedule.setAcademicSemester(null);
        }
        if (request.subjectId() != null) {
            var subj = subjectRepository.findById(request.subjectId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Subject not found with id: " + request.subjectId()));
            schedule.setSubject(subj);
        } else {
            schedule.setSubject(null);
        }
        if (request.professorId() != null) {
            var u = userRepository.findById(request.professorId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.professorId()));
            schedule.setProfessor(u);
        } else {
            schedule.setProfessor(null);
        }
        schedule = repository.save(schedule);
        return ScheduleMapper.toResponse(schedule);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Schedule not found with id: " + id);
        }
        repository.deleteById(id);
    }

}
