package com.fatec.horario.web.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.horario.domain.services.DisciplinaService;
import com.fatec.horario.dto.disciplina.DisciplinaRequest;
import com.fatec.horario.dto.disciplina.DisciplinaResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/disciplinas")
@CrossOrigin
public class DisciplinaController {

    @Autowired
    private DisciplinaService service;

    @GetMapping
    public ResponseEntity<Page<DisciplinaResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(name = "curso", required = false) Long idCurso,
            @RequestParam(name = "tipo_sala", required = false) Long idTipoSala,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.listar(nome, idCurso, idTipoSala, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisciplinaResponse> buscar(@PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<DisciplinaResponse> criar(
            @Valid @RequestBody DisciplinaRequest request) {

        DisciplinaResponse response = service.criar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisciplinaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DisciplinaRequest request) {

        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        service.deletar(id);

        return ResponseEntity.noContent().build();
    }
}