package com.fatec.horario.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.model.ShiftSchedule;
import com.fatec.horario.dto.ShiftScheduleRequest;
import com.fatec.horario.dto.ShiftScheduleResponse;
import com.fatec.horario.infrastructure.mapper.ShiftScheduleMapper;
import com.fatec.horario.infrastructure.repository.ShiftScheduleRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ShiftScheduleService {

    @Autowired
    ShiftScheduleRepository repository;

    public List<ShiftScheduleResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(ShiftScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ShiftScheduleResponse getById(Long id) {
        ShiftSchedule entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ShiftSchedule not found with id: " + id));

        return ShiftScheduleMapper.toResponse(entity);
    }

    public ShiftScheduleResponse create(ShiftScheduleRequest request) {
        ShiftSchedule shiftSchedule = ShiftScheduleMapper.toEntity(request);
        shiftSchedule = repository.save(shiftSchedule);
        return ShiftScheduleMapper.toResponse(shiftSchedule);
    }

    public ShiftScheduleResponse update(Long id, ShiftScheduleRequest request) {
        ShiftSchedule shiftSchedule = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id: " + id));

        shiftSchedule.setLessonCount(request.lessonCount());
        shiftSchedule.setLessonDuration(request.lessonDuration());
        shiftSchedule.setShiftDescription(request.shiftDescription());
        shiftSchedule.setStartTime(request.startTime());
        shiftSchedule.setIncludedSaturday(request.includedSaturday());
        shiftSchedule = repository.save(shiftSchedule);
        return ShiftScheduleMapper.toResponse(shiftSchedule);
    }

    public void delete(Long id) {
        ShiftSchedule existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ShiftSchedule not found with id: " + id));

        repository.delete(existing);
    }
}
