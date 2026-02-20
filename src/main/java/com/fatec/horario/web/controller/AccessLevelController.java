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

import com.fatec.horario.dto.AccessLevelRequest;
import com.fatec.horario.dto.AccessLevelResponse;
import com.fatec.horario.domain.service.AccessLevelService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/access-levels")
@CrossOrigin
public class AccessLevelController {

    @Autowired
    private AccessLevelService service;

    @GetMapping
    public ResponseEntity<List<AccessLevelResponse>> getAll() {
        List<AccessLevelResponse> accessLevels = service.getAll();
        return ResponseEntity.ok(accessLevels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccessLevelResponse> getById(@PathVariable Long id) {
        AccessLevelResponse accessLevel = service.getById(id);
        return ResponseEntity.ok(accessLevel);
    }

    @PostMapping
    public ResponseEntity<AccessLevelResponse> create(
            @Valid @RequestBody AccessLevelRequest request) {
        AccessLevelResponse accessLevel = service.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(accessLevel.id())
                .toUri();
        return ResponseEntity.created(location).body(accessLevel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccessLevelResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AccessLevelRequest request) {
        AccessLevelResponse accessLevel = service.update(id, request);
        return ResponseEntity.ok(accessLevel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
