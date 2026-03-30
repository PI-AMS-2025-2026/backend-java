package com.fatec.horario.web.controller;

import com.fatec.horario.domain.services.CursoService;
import com.fatec.horario.dto.curso.CursoRequest;
import com.fatec.horario.dto.curso.CursoResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/curso")
public class CursoController {

    private final CursoService service;

    public CursoController(CursoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CursoResponse> criar(@RequestBody @Valid CursoRequest dto) {
        CursoResponse response = service.criar(dto);

        return ResponseEntity
                .created(URI.create("/curso/" + response.getId()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CursoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CursoRequest dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}