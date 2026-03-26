package com.fatec.horario.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.horario.domain.services.RecursoService;
import com.fatec.horario.dto.Recurso.RecursoRequest;
import com.fatec.horario.dto.Recurso.RecursoResponse;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/recursos")
@CrossOrigin
public class RecursoController {

    @Autowired
    private RecursoService service;

    // Criar recurso
    @PostMapping
    public ResponseEntity<RecursoResponse> criar(@Valid @RequestBody RecursoRequest request) {
        return ResponseEntity.ok(service.criar(request));
    }

    // Listar com filtros
    @GetMapping
    public ResponseEntity<List<RecursoResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String tipo) {

        return ResponseEntity.ok(service.listar(nome, tipo));
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<RecursoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<RecursoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RecursoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    // Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}