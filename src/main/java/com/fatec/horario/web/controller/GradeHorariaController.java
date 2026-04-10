package com.fatec.horario.web.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.horario.domain.entities.Status; 
import com.fatec.horario.domain.services.GradeHorariaService;
import com.fatec.horario.dto.gradeHoraria.GradeHorariaRequest;
import com.fatec.horario.dto.gradeHoraria.GradeHorariaResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/grades-horarias")
@CrossOrigin
public class GradeHorariaController {

    @Autowired
    private GradeHorariaService service;

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