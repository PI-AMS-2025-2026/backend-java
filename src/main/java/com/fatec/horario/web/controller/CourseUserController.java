package com.fatec.horario.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.horario.dto.CourseUserRequest;
import com.fatec.horario.dto.CourseUserResponse;
import com.fatec.horario.domain.service.CourseUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/course-users")
@CrossOrigin
public class CourseUserController {

    @Autowired
    private CourseUserService service;

    @GetMapping
    public ResponseEntity<List<CourseUserResponse>> getAll() {
        List<CourseUserResponse> courseUsers = service.getAll();
        return ResponseEntity.ok(courseUsers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseUserResponse> getById(@PathVariable Long id) {
        CourseUserResponse courseUser = service.getById(id);
        return ResponseEntity.ok(courseUser);
    }

    @PostMapping
    public ResponseEntity<CourseUserResponse> create(@Valid @RequestBody CourseUserRequest request) {
        CourseUserResponse courseUser = service.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(courseUser.id())
                .toUri();
        return ResponseEntity.created(location).body(courseUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseUserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CourseUserRequest request) {
        CourseUserResponse courseUser = service.update(id, request);
        return ResponseEntity.ok(courseUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
