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

import com.fatec.horario.dto.ModalityRequest;
import com.fatec.horario.dto.ModalityResponse;
import com.fatec.horario.domain.service.ModalityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/modalities")
@CrossOrigin
public class ModalityController {

    @Autowired
    private ModalityService service;

    @GetMapping
    public ResponseEntity<List<ModalityResponse>> getAll() {
        List<ModalityResponse> modalities = service.getAll();
        return ResponseEntity.ok(modalities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModalityResponse> getById(@PathVariable Long id) {
        ModalityResponse modality = service.getById(id);
        return ResponseEntity.ok(modality);
    }

    @PostMapping
    public ResponseEntity<ModalityResponse> create(
            @Valid @RequestBody ModalityRequest request) {

        ModalityResponse modality = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(modality.id())
                .toUri();

        return ResponseEntity.created(location).body(modality);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModalityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ModalityRequest request) {

        ModalityResponse modality = service.update(request, id);
        return ResponseEntity.ok(modality);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
