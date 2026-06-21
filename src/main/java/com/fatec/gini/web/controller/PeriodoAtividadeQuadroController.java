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

import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.domain.services.PeriodoAtividadeQuadroService;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroRequest;
import com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/periodo_atividade_quadro")
@RequiredArgsConstructor
public class PeriodoAtividadeQuadroController {

    private final PeriodoAtividadeQuadroService service;

    @GetMapping
    public ResponseEntity<PageResponse<PeriodoAtividadeQuadroResponse>> listar(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer periodo,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) java.time.LocalDate dataInicio,
            @RequestParam(required = false) java.time.LocalDate dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.listar(ano, periodo, status, dataInicio, dataFim, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodoAtividadeQuadroResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PeriodoAtividadeQuadroResponse> criar(
            @Valid @RequestBody PeriodoAtividadeQuadroRequest request) {

        PeriodoAtividadeQuadroResponse response = service.criar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.idPeriodoAtividadeQuadro())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeriodoAtividadeQuadroResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PeriodoAtividadeQuadroRequest request) {

        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

}
