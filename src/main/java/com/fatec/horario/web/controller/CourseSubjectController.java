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

import com.fatec.horario.dto.CourseSubjectRequest;
import com.fatec.horario.dto.CourseSubjectResponse;
import com.fatec.horario.domain.service.CourseSubjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/course-subjects")
@CrossOrigin
public class CourseSubjectController {

    @Autowired
    private CourseSubjectService service;

    @GetMapping
    public ResponseEntity<List<CourseSubjectResponse>> getAll() {
        List<CourseSubjectResponse> courseSubjects = service.getAll();
        return ResponseEntity.ok(courseSubjects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseSubjectResponse> getById(@PathVariable Long id) {
        CourseSubjectResponse courseSubject = service.getById(id);
        return ResponseEntity.ok(courseSubject);
    }

    @PostMapping
    public ResponseEntity<CourseSubjectResponse> create(@Valid @RequestBody CourseSubjectRequest request) {
        CourseSubjectResponse courseSubject = service.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(courseSubject.id())
                .toUri();
        return ResponseEntity.created(location).body(courseSubject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseSubjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CourseSubjectRequest request) {
        CourseSubjectResponse courseSubject = service.update(id, request);
        return ResponseEntity.ok(courseSubject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
