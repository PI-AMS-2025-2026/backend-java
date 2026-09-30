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

import com.fatec.gini.domain.services.TurmaService;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.turma.TurmaRequest;
import com.fatec.gini.dto.turma.TurmaResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService service;

    @GetMapping
    public ResponseEntity<PageResponse<TurmaResponse>> listar(
            @RequestParam(name = "curso", required = false) Long idCurso,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer periodo,
            @RequestParam(required = false) String codigo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.listar(
                        idCurso,
                        ano,
                        periodo,
                        codigo,
                        page,
                        size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponse> buscar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TurmaResponse> criar(
            @Valid @RequestBody TurmaRequest request) {

        TurmaResponse response = service.criar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TurmaRequest request) {

        return ResponseEntity.ok(
                service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id) {

        service.deletar(id);

        return ResponseEntity.noContent().build();
    }
}