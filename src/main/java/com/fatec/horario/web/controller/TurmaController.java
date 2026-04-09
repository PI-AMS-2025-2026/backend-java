package com.fatec.horario.web.controller;

import com.fatec.horario.domain.services.TurmaService;
import com.fatec.horario.dto.Turma.TurmaRequest;
import com.fatec.horario.dto.Turma.TurmaResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/turmas")
@CrossOrigin
public class TurmaController {

    @Autowired
    private TurmaService service;

    @GetMapping
    public ResponseEntity<Page<TurmaResponse>> listar(
            @RequestParam(name = "curso", required = false) Long idCurso,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer periodo,
            @RequestParam(required = false) String codigo, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.listar(idCurso, ano, periodo, codigo, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TurmaResponse> criar(@Valid @RequestBody TurmaRequest request) {
        TurmaResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TurmaRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}