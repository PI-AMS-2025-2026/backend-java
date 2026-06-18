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
import com.fatec.gini.domain.services.QuadroHorarioService;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioRequest;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/quadro-horarios")
@RequiredArgsConstructor
public class QuadroHorarioController {

    private final QuadroHorarioService service;

    @GetMapping
    public ResponseEntity<Page<QuadroHorarioResponse>> listar(
            @RequestParam(name = "curso", required = false) Long idCurso,
            @RequestParam(name = "periodo_atividade_quadro", required = false) Long idPeriodoAtividadeQuadro,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.listar(idCurso, idPeriodoAtividadeQuadro, status, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuadroHorarioResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<QuadroHorarioResponse> criar(
            @Valid @RequestBody QuadroHorarioRequest request) {

        QuadroHorarioResponse response = service.criar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("{id}/copiar")
    public ResponseEntity<QuadroHorarioResponse> copiar(@PathVariable Long id,
            @Valid @RequestBody QuadroHorarioRequest request) {

        QuadroHorarioResponse response = service.copiar(id, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuadroHorarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody QuadroHorarioRequest request) {

        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}