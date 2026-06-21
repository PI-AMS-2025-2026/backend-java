package com.fatec.gini.web.controller;

import java.net.URI;
import java.time.LocalTime;
import java.util.List;

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

import com.fatec.gini.domain.services.BlocoHorarioService;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioRequest;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioResponse;
import com.fatec.gini.dto.paginacao.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bloco-horarios")
@RequiredArgsConstructor
public class BlocoHorarioController {

    private final BlocoHorarioService service;

    @GetMapping
    public ResponseEntity<PageResponse<BlocoHorarioResponse>> listar(
            @RequestParam(value = "hora_inicio", required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime horaInicio,
            @RequestParam(value = "hora_fim", required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime horaFim,
            @RequestParam(required = false) Integer duracao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.listar(horaInicio, horaFim, duracao, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlocoHorarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<BlocoHorarioResponse> criar(@Valid @RequestBody BlocoHorarioRequest request) {
        BlocoHorarioResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/lote")
    public ResponseEntity<List<BlocoHorarioResponse>> criarLote(@Valid @RequestBody List<BlocoHorarioRequest> request) {

        List<BlocoHorarioResponse> response = service.criarLote(request);
   
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlocoHorarioResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody BlocoHorarioRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
