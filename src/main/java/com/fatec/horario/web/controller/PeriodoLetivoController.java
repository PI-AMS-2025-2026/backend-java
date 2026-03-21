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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.horario.domain.services.PeriodoLetivoService;
import com.fatec.horario.dto.PeriodoLetivo.PeriodoLetivoRequest;
import com.fatec.horario.dto.PeriodoLetivo.PeriodoLetivoResponse;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/periodos-letivos")
@CrossOrigin
public class PeriodoLetivoController {

    @Autowired
    private PeriodoLetivoService service;

    @PostMapping
    public ResponseEntity<PeriodoLetivoResponse> criar(
            @Valid @RequestBody PeriodoLetivoRequest request) {

        PeriodoLetivoResponse response = service.criar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.idPeriodoLetivo())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodoLetivoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<PeriodoLetivoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeriodoLetivoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PeriodoLetivoRequest request) {

        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
