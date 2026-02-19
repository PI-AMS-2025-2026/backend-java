package com.fatec.horario.controllers;

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

import com.fatec.horario.dtos.AcademicSemesterRequest;
import com.fatec.horario.dtos.AcademicSemesterResponse;
import com.fatec.horario.services.AcademicSemesterService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/academic-semesters")
@CrossOrigin
public class AcademicSemesterController {

    @Autowired
    private AcademicSemesterService service;

    @GetMapping
    public ResponseEntity<List<AcademicSemesterResponse>> getAll() {
        List<AcademicSemesterResponse> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicSemesterResponse> getById(@PathVariable Long id) {
        AcademicSemesterResponse response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AcademicSemesterResponse> create(
            @Valid @RequestBody AcademicSemesterRequest request) {
        AcademicSemesterResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcademicSemesterResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AcademicSemesterRequest request) {
        AcademicSemesterResponse updated = service.update(request, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteAcademicSemesterById(id);
        return ResponseEntity.noContent().build();
    }
}
