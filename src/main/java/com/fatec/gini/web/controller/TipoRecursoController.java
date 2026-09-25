package com.fatec.gini.web.controller;

import java.net.URI;
import java.util.List;

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

import com.fatec.gini.domain.services.TipoRecursoService;
import com.fatec.gini.dto.tipoRecurso.TipoRecursoRequest;
import com.fatec.gini.dto.tipoRecurso.TipoRecursoResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tipos-recurso")
@RequiredArgsConstructor
public class TipoRecursoController {

    private final TipoRecursoService service;

    @GetMapping
    public ResponseEntity<List<TipoRecursoResponse>> listar(
            @RequestParam(required = false) String nome) {
        return ResponseEntity.ok(service.listar(nome));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoRecursoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoRecursoResponse> criar(@Valid @RequestBody TipoRecursoRequest request) {
        TipoRecursoResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoRecursoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TipoRecursoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}