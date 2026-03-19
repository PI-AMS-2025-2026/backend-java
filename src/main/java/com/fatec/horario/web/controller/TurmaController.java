package com.fatec.horario.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.horario.domain.services.TurmaService;
import com.fatec.horario.dto.Turma.TurmaRequest;
import com.fatec.horario.dto.Turma.TurmaResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/turmas")
@CrossOrigin
public class TurmaController {

    @Autowired
    private TurmaService service;

    @GetMapping
    public ResponseEntity<List<TurmaResponse>> getAll() {
        List<TurmaResponse> turmas = service.getAll();
        return ResponseEntity.ok(turmas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponse> getById(@PathVariable Long id) {
        TurmaResponse turma = service.getById(id);
        return ResponseEntity.ok(turma);
    }

    @PostMapping
    public ResponseEntity<TurmaResponse> create(
            @Valid @RequestBody TurmaRequest request) {

        TurmaResponse turma = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(turma.id())
                .toUri();

        return ResponseEntity.created(location).body(turma);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TurmaRequest request) {

        TurmaResponse turma = service.update(request, id);
        return ResponseEntity.ok(turma);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
