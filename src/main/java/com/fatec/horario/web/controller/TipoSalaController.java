package com.fatec.horario.web.controller;

import java.net.URI;
import java.util.List;

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

import com.fatec.horario.domain.services.TipoSalaService;
import com.fatec.horario.dto.tipoSala.TipoSalaRequest;
import com.fatec.horario.dto.tipoSala.TipoSalaResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tipos-sala")
@CrossOrigin
public class TipoSalaController {

    @Autowired
    private TipoSalaService service;

    @GetMapping
    public ResponseEntity<List<TipoSalaResponse>> listar(
            @RequestParam(required = false) String nome) {
        return ResponseEntity.ok(service.listar(nome));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoSalaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoSalaResponse> criar(@Valid @RequestBody TipoSalaRequest request) {
        TipoSalaResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoSalaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TipoSalaRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}