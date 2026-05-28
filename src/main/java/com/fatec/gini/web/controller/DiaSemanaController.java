package com.fatec.gini.web.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.gini.domain.services.DiaSemanaService;
import com.fatec.gini.dto.diaSemana.DiaSemanaRequest;
import com.fatec.gini.dto.diaSemana.DiaSemanaResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/dias-semana")
@CrossOrigin
public class DiaSemanaController {

    @Autowired
    private DiaSemanaService service;

    @GetMapping
    public ResponseEntity<List<DiaSemanaResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaSemanaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<DiaSemanaResponse> criar(@Valid @RequestBody DiaSemanaRequest request) {
        DiaSemanaResponse response = service.criar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaSemanaResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody DiaSemanaRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}