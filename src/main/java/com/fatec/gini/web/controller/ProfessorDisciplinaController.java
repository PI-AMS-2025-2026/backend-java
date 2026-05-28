package com.fatec.gini.web.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.gini.domain.services.ProfessorDisciplinaService;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaResponse;

import java.net.URI;

@RestController
@RequestMapping("/professores-disciplinas")
@CrossOrigin
public class ProfessorDisciplinaController {

    @Autowired
    private ProfessorDisciplinaService service;

    // CREATE
    @PostMapping
    public ResponseEntity<ProfessorDisciplinaResponse> criar(
            @RequestBody @Valid ProfessorDisciplinaRequest request) {

        ProfessorDisciplinaResponse response = service.criar(request);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorDisciplinaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // LIST + FILTROS + PAGINAÇÃO
    @GetMapping
    public ResponseEntity<Page<ProfessorDisciplinaResponse>> listar(
            @RequestParam(required = false) Long usuario,
            @RequestParam(required = false) Long disciplina,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.listar(usuario, disciplina, page, size));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorDisciplinaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ProfessorDisciplinaRequest request) {

        return ResponseEntity.ok(service.atualizar(id, request));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
