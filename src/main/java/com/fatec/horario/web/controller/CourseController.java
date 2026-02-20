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

import com.fatec.horario.dto.CourseRequest;
import com.fatec.horario.dto.CourseResponse;
import com.fatec.horario.domain.service.CourseService;

import jakarta.validation.Valid;

@RestController

@RequestMapping("/courses")

@CrossOrigin

public class CourseController {

    @Autowired

    private CourseService service;

    @GetMapping

    public ResponseEntity<List<CourseResponse>> getAll() {

        List<CourseResponse> courses = service.getAll();

        return ResponseEntity.ok(courses);

    }

    @GetMapping("/{id}")

    public ResponseEntity<CourseResponse> getById(@PathVariable Long id) {

        CourseResponse course = service.getById(id);

        return ResponseEntity.ok(course);

    }

    @PostMapping

    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {

        CourseResponse course = service.create(request);

        URI location = ServletUriComponentsBuilder

                .fromCurrentRequest()

                .path("/{id}")

                .buildAndExpand(course.id())

                .toUri();

        return ResponseEntity.created(location).body(course);

    }

    @PutMapping("/{id}")

    public ResponseEntity<CourseResponse> update(

            @PathVariable Long id,

            @Valid @RequestBody CourseRequest request) {

        CourseResponse course = service.update(id, request);

        return ResponseEntity.ok(course);

    }

    @DeleteMapping("/{id}")

    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();

    }

}
