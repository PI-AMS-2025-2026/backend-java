package com.fatec.gini.web.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.gini.domain.services.RecursoSalaService;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.recursoSala.RecursoSalaRequest;
import com.fatec.gini.dto.recursoSala.RecursoSalaResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recurso-sala")
@RequiredArgsConstructor
public class RecursoSalaController {

    private final RecursoSalaService service;

    @PostMapping
    public ResponseEntity<RecursoSalaResponse> criar(@Valid @RequestBody RecursoSalaRequest request) {
        var response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecursoSalaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<RecursoSalaResponse>> listar(
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