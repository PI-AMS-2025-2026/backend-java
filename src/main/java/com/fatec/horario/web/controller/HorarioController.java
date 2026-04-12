package com.fatec.horario.web.controller;

import com.fatec.horario.domain.services.HorarioService;
import com.fatec.horario.dto.Horarios.HorarioRequest;
import com.fatec.horario.dto.Horarios.HorarioResponse;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.net.URI;
import java.time.LocalTime;

@RestController
@RequestMapping("/horarios")
public class HorarioController {

    @Autowired
    private HorarioService service;

    @GetMapping
    public ResponseEntity<Page<HorarioResponse>> listar(
            @RequestParam(value = "hora_inicio", required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime horaInicio,
            @RequestParam(value = "hora_fim", required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime horaFim,
            @RequestParam(required = false) int duracao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.listar(horaInicio, horaFim, duracao, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<HorarioResponse> criar(@Valid @RequestBody HorarioRequest request) {
        HorarioResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody HorarioRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
