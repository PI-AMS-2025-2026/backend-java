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

import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.CursoService;
import com.fatec.gini.dto.curso.CursoRequest;
import com.fatec.gini.dto.curso.CursoResponse;
import com.fatec.gini.dto.paginacao.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService service;

    @GetMapping
    public ResponseEntity<PageResponse<CursoResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String periodicidade,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Integer duracao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.listar(nome, periodicidade, status, duracao,page,size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CursoResponse> criar(@RequestBody @Valid CursoRequest request) {
        CursoResponse response = service.criar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CursoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}