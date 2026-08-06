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

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.services.DisponibilidadeProfessorService;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorResponse;
import com.fatec.gini.dto.paginacao.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/disponibilidades-professores")
@RequiredArgsConstructor
public class DisponibilidadeProfessorController {

    private final DisponibilidadeProfessorService service;

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
    public ResponseEntity<PageResponse<DisponibilidadeProfessorResponse>> listar(
            @RequestParam(required = false) Long professor,
            @RequestParam(required = false, name = "dia_semana") DiaSemana diaSemana,
            @RequestParam(required = false, name = "bloco_horario") Long blocoHorario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.listar(professor, diaSemana, blocoHorario, page,size));
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
