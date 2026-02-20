package com.fatec.horario.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.model.ShiftSchedule;
import com.fatec.horario.domain.model.ShiftScheduleDetail;
import com.fatec.horario.dto.ShiftScheduleDetail.ShiftScheduleDetailRequest;
import com.fatec.horario.dto.ShiftScheduleDetail.ShiftScheduleDetailResponse;
import com.fatec.horario.infrastructure.mapper.ShiftScheduleDetailMapper;
import com.fatec.horario.infrastructure.repository.ShiftScheduleDetailRepository;
import com.fatec.horario.infrastructure.repository.ShiftScheduleRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ShiftScheduleDetailService {

    @Autowired
    private ShiftScheduleDetailRepository shiftScheduleDetailRepository;

    @Autowired
    private ShiftScheduleRepository shiftScheduleRepository;

    public List<ShiftScheduleDetailResponse> getAll() {
        return shiftScheduleDetailRepository.findAll()
                .stream()
                .map(ShiftScheduleDetailMapper::toResponse)
                .toList();
    }

    public ShiftScheduleDetailResponse getById(Long id) {
        ShiftScheduleDetail detail = shiftScheduleDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ShiftScheduleDetail not found with id: " + id));
        return ShiftScheduleDetailMapper.toResponse(detail);
    }

    public ShiftScheduleDetailResponse create(ShiftScheduleDetailRequest request) {
        ShiftScheduleDetail detail = ShiftScheduleDetailMapper.toEntity(request);

        if (request.shiftScheduleId() != null) {
            ShiftSchedule shiftSchedule = shiftScheduleRepository.findById(request.shiftScheduleId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "ShiftSchedule not found with id: " + request.shiftScheduleId()));
            detail.setShiftSchedule(shiftSchedule);
        }

        detail = shiftScheduleDetailRepository.save(detail);
        return ShiftScheduleDetailMapper.toResponse(detail);
    }

    public ShiftScheduleDetailResponse update(Long id, ShiftScheduleDetailRequest request) {
        ShiftScheduleDetail detail = shiftScheduleDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ShiftScheduleDetail not found with id: " + id));

        detail.setLessonNumber(request.lessonNumber());
        detail.setStartTime(request.startTime());
        detail.setEndTime(request.endTime());

        if (request.shiftScheduleId() != null) {
            ShiftSchedule shiftSchedule = shiftScheduleRepository.findById(request.shiftScheduleId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "ShiftSchedule not found with id: " + request.shiftScheduleId()));
            detail.setShiftSchedule(shiftSchedule);
        }

        detail = shiftScheduleDetailRepository.save(detail);
        return ShiftScheduleDetailMapper.toResponse(detail);
    }

    public void delete(Long id) {
        if (!shiftScheduleDetailRepository.existsById(id)) {
            throw new EntityNotFoundException("ShiftScheduleDetail not found with id: " + id);
        }
        shiftScheduleDetailRepository.deleteById(id);
    }
}
