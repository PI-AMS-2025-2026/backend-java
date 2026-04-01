package com.fatec.horario.web.controller;

import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import com.fatec.horario.domain.services.DisciplinaService;
import com.fatec.horario.dto.Disciplina.DisciplinaRequest;
import com.fatec.horario.dto.Disciplina.DisciplinaResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/disciplinas")
@CrossOrigin
public class DisciplinaController {

    @Autowired
    private DisciplinaService service;

    @PostMapping
    public ResponseEntity<DisciplinaResponse> criar(
            @Valid @RequestBody DisciplinaRequest request) {

        DisciplinaResponse response = service.criar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.idDisciplina())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<DisciplinaResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false, name = "curso") Long idCurso,
            @RequestParam(required = false, name = "tipo_sala") Long idTipoSala,
            Pageable pageable) {

        return ResponseEntity.ok(service.listar(nome, idCurso, idTipoSala, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisciplinaResponse> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
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