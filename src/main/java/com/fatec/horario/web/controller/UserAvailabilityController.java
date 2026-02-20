package com.fatec.horario.web.controller;

import com.fatec.horario.dto.UserAvailabilityRequest;
import com.fatec.horario.dto.UserAvailabilityResponse;
import com.fatec.horario.domain.service.UserAvailabilityService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/user-availability")
@CrossOrigin
public class UserAvailabilityController {

    @Autowired
    private UserAvailabilityService service;

    @GetMapping
    public ResponseEntity<List<UserAvailabilityResponse>> getAll() {
        List<UserAvailabilityResponse> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAvailabilityResponse> getById(@PathVariable Long id) {
        UserAvailabilityResponse response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserAvailabilityResponse> create(
            @Valid @RequestBody UserAvailabilityRequest request) {

        UserAvailabilityResponse response = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAvailabilityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UserAvailabilityRequest request) {

        UserAvailabilityResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
