package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.Course;
import com.fatec.horario.domain.entities.CourseUser;
import com.fatec.horario.domain.entities.User;
import com.fatec.horario.dto.CourseUser.CourseUserRequest;
import com.fatec.horario.dto.CourseUser.CourseUserResponse;
import com.fatec.horario.infrastructure.mappers.CourseUserMapper;
import com.fatec.horario.infrastructure.repositories.CourseRepository;
import com.fatec.horario.infrastructure.repositories.CourseUserRepository;
import com.fatec.horario.infrastructure.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CourseUserService {

    @Autowired
    private CourseUserRepository courseUserRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CourseUserResponse> getAll() {
        return courseUserRepository.findAll()
                .stream()
                .map(CourseUserMapper::toResponse)
                .toList();
    }

    public CourseUserResponse getById(Long id) {
        CourseUser courseUser = courseUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CourseUser not found with id: " + id));
        return CourseUserMapper.toResponse(courseUser);
    }

    public CourseUserResponse create(CourseUserRequest request) {
        CourseUser courseUser = CourseUserMapper.toEntity(request);

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.userId()));
            courseUser.setUser(user);
        }

        if (request.courseId() != null) {
            Course course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + request.courseId()));
            courseUser.setCourse(course);
        }

        courseUser = courseUserRepository.save(courseUser);
        return CourseUserMapper.toResponse(courseUser);
    }

    public CourseUserResponse update(Long id, CourseUserRequest request) {
        CourseUser courseUser = courseUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CourseUser not found with id: " + id));

        courseUser.setRole(request.role());

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.userId()));
            courseUser.setUser(user);
        }

        if (request.courseId() != null) {
            Course course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + request.courseId()));
            courseUser.setCourse(course);
        }

        courseUser = courseUserRepository.save(courseUser);
        return CourseUserMapper.toResponse(courseUser);
    }

    public void delete(Long id) {
        if (!courseUserRepository.existsById(id)) {
            throw new EntityNotFoundException("CourseUser not found with id: " + id);
        }
        courseUserRepository.deleteById(id);
    }
}
