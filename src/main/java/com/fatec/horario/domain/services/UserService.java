package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.AccessLevel;
import com.fatec.horario.domain.entities.User;
import com.fatec.horario.dto.User.UserRequest;
import com.fatec.horario.dto.User.UserResponse;
import com.fatec.horario.infrastructure.mappers.UserMapper;
import com.fatec.horario.infrastructure.repositories.AccessLevelRepository;
import com.fatec.horario.infrastructure.repositories.CourseUserRepository;
import com.fatec.horario.infrastructure.repositories.ScheduleRepository;
import com.fatec.horario.infrastructure.repositories.UserAvailabilityRepository;
import com.fatec.horario.infrastructure.repositories.UserRepository;
import com.fatec.horario.infrastructure.repositories.UserSubjectRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccessLevelRepository accessLevelRepository;

    @Autowired
    private CourseUserRepository courseUserRepository;

    @Autowired
    private UserSubjectRepository userSubjectRepository;
    @Autowired
    private UserAvailabilityRepository userAvailabilityRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return UserMapper.toResponse(user);
    }

    public UserResponse create(UserRequest request) {
        User user = UserMapper.toEntity(request);
        if (request.accessLevelId() != null) {
            AccessLevel accessLevel = accessLevelRepository.findById(request.accessLevelId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Acess Level not found with id: " + request.accessLevelId()));
            user.setAccessLevel(accessLevel);
        }

        user = userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());

        if (request.accessLevelId() != null) {
            AccessLevel accessLevel = accessLevelRepository.findById(request.accessLevelId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Acess Level not found with id: " + request.accessLevelId()));
            user.setAccessLevel(accessLevel);
        } else {
            user.setAccessLevel(null);
        }

        user = userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userSubjectRepository.deleteByUserId(id);
        courseUserRepository.deleteByUserId(id);
        userAvailabilityRepository.deleteByUserId(id);
        scheduleRepository.deleteByProfessorId(id);
        userRepository.deleteById(id);
    }
}
