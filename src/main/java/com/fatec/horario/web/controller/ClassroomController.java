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

import com.fatec.horario.domain.service.ClassroomService;
import com.fatec.horario.dto.Classroom.ClassroomRequest;
import com.fatec.horario.dto.Classroom.ClassroomResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/classrooms")
@CrossOrigin
public class ClassroomController {

    @Autowired
    private ClassroomService service;

    @GetMapping
    public ResponseEntity<List<ClassroomResponse>> getAll() {
        List<ClassroomResponse> classrooms = service.getAll();
        return ResponseEntity.ok(classrooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomResponse> getById(@PathVariable Long id) {
        ClassroomResponse classroom = service.getById(id);
        return ResponseEntity.ok(classroom);
    }

    @PostMapping
    public ResponseEntity<ClassroomResponse> save(
            @Valid @RequestBody ClassroomRequest request) {

        ClassroomResponse classroom = service.save(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(classroom.id())
                .toUri();

        return ResponseEntity.created(location).body(classroom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassroomResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ClassroomRequest request) {

        ClassroomResponse classroom = service.update(id, request);
        return ResponseEntity.ok(classroom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
