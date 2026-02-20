package com.fatec.horario.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.model.User;
import com.fatec.horario.domain.model.UserAvailability;
import com.fatec.horario.dto.UserAvailabilityRequest;
import com.fatec.horario.dto.UserAvailabilityResponse;
import com.fatec.horario.infrastructure.mapper.UserAvailabilityMapper;
import com.fatec.horario.infrastructure.repository.UserAvailabilityRepository;
import com.fatec.horario.infrastructure.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserAvailabilityService {

    @Autowired
    private UserAvailabilityRepository repository;
    @Autowired
    private UserRepository userRepository;

    public List<UserAvailabilityResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(UserAvailabilityMapper::toResponse)
                .collect(Collectors.toList());
    }

    public UserAvailabilityResponse getById(Long id) {
        UserAvailability userAvailability = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserAvailability not found with id " + id));

        return UserAvailabilityMapper.toResponse(userAvailability);
    }

    public UserAvailabilityResponse create(UserAvailabilityRequest request) {
        UserAvailability userAvailability = UserAvailabilityMapper.toEntity(request);
        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.userId()));
            userAvailability.setUser(user);
        }
        userAvailability = repository.save(userAvailability);

        return UserAvailabilityMapper.toResponse(userAvailability);
    }

    public UserAvailabilityResponse update(Long id, UserAvailabilityRequest request) {
        UserAvailability userAvailability = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserAvailability not found with id " + id));

        userAvailability.setWeekday(request.weekday());
        userAvailability.setLessonNumber(request.lessonNumber());
        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.userId()));
            userAvailability.setUser(user);
        } else {
            userAvailability.setUser(null);
        }

        userAvailability = repository.save(userAvailability);

        return UserAvailabilityMapper.toResponse(userAvailability);
    }

    public void delete(Long id) {
        UserAvailability userAvailability = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserAvailability not found with id " + id));
        repository.delete(userAvailability);
    }
}
