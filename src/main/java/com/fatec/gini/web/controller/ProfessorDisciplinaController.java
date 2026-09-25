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

import com.fatec.gini.domain.services.ProfessorDisciplinaService;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/professores-disciplinas")
@RequiredArgsConstructor
public class ProfessorDisciplinaController {

    private final ProfessorDisciplinaService service;

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
    public ResponseEntity<PageResponse<ProfessorDisciplinaResponse>> listar(
            // Usa o mesmo nome do relacionamento exposto pela API.
            @RequestParam(required = false) Long professor,
            @RequestParam(required = false) Long disciplina,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.listar(professor, disciplina, page, size));
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
