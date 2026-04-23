package com.fatec.horario.web.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.horario.domain.services.DisponibilidadeProfessorService;
import com.fatec.horario.dto.disponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.horario.dto.disponibilidadeProfessor.DisponibilidadeProfessorResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/disponibilidades-professores")
@CrossOrigin
public class DisponibilidadeProfessorController {

    @Autowired
    private DisponibilidadeProfessorService service;

    @PostMapping
    public ResponseEntity<DisponibilidadeProfessorResponse> criar(
            @RequestBody @Valid DisponibilidadeProfessorRequest request) {
        DisponibilidadeProfessorResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<DisponibilidadeProfessorResponse>> listar(
            @RequestParam(required = false) Long usuario,
            @RequestParam(required = false, name = "dia_semana") Long diaSemana,
            @RequestParam(required = false) Long horario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.listar(usuario, diaSemana, horario, page,size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadeProfessorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadeProfessorResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DisponibilidadeProfessorRequest request) {

        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
