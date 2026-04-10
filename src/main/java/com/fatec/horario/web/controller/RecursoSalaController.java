package com.fatec.horario.web.controller;


import com.fatec.horario.domain.services.RecursoSalaService;
import com.fatec.horario.dto.RecursoSala.RecursoSalaRequest;
import com.fatec.horario.dto.RecursoSala.RecursoSalaResponse;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/recurso-sala")
@CrossOrigin
public class RecursoSalaController {

    private final RecursoSalaService service;

    public RecursoSalaController(RecursoSalaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RecursoSalaResponse> criar(@Valid @RequestBody RecursoSalaRequest request) {
        var response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecursoSalaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<RecursoSalaResponse>> listar(
            @RequestParam(required = false) Long salaId,
            @RequestParam(required = false) Long recursoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.listar(salaId, recursoId, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecursoSalaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RecursoSalaRequest request) {

        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}