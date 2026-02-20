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

import com.fatec.horario.domain.service.PeriodicityService;
import com.fatec.horario.dto.Periodicity.PeriodicityRequest;
import com.fatec.horario.dto.Periodicity.PeriodicityResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/periodicities")
@CrossOrigin
public class PeriodicityController {

    @Autowired
    private PeriodicityService service;

    @GetMapping
    public ResponseEntity<List<PeriodicityResponse>> getAll() {
        List<PeriodicityResponse> periodicities = service.getAll();
        return ResponseEntity.ok(periodicities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodicityResponse> getById(@PathVariable Long id) {
        PeriodicityResponse periodicity = service.getById(id);
        return ResponseEntity.ok(periodicity);
    }

    @PostMapping
    public ResponseEntity<PeriodicityResponse> create(
            @Valid @RequestBody PeriodicityRequest request) {

        PeriodicityResponse created = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeriodicityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PeriodicityRequest request) {

        PeriodicityResponse updated = service.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
