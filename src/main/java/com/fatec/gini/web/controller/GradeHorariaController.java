package com.fatec.gini.web.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
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

import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.domain.services.GradeHorariaService;
import com.fatec.gini.dto.gradeHoraria.GradeHorariaRequest;
import com.fatec.gini.dto.gradeHoraria.GradeHorariaResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/grades-horarias")
@RequiredArgsConstructor
public class GradeHorariaController {

    private final GradeHorariaService service;

    @GetMapping
    public ResponseEntity<Page<GradeHorariaResponse>> listar(
            @RequestParam(name = "curso", required = false) Long idCurso,
            @RequestParam(name = "periodo_letivo", required = false) Long idPeriodoLetivo,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.listar(idCurso, idPeriodoLetivo, status, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeHorariaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<GradeHorariaResponse> criar(
            @Valid @RequestBody GradeHorariaRequest request) {

        GradeHorariaResponse response = service.criar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeHorariaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody GradeHorariaRequest request) {

        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}