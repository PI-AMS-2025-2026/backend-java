package com.fatec.horario.web.controller;

import com.fatec.horario.domain.services.TipoUsuarioService;
import com.fatec.horario.dto.tipoUsuario.TipoUsuarioRequest;
import com.fatec.horario.dto.tipoUsuario.TipoUsuarioResponse;

import jakarta.validation.Valid;

import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/tipo-usuario")
public class TipoUsuarioController {

    private final TipoUsuarioService service;

    public TipoUsuarioController(TipoUsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TipoUsuarioResponse> criar(@RequestBody @Valid TipoUsuarioRequest dto) {

        TipoUsuarioResponse response = service.criar(dto);

        return ResponseEntity
                .created(URI.create("/tipo-usuario/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TipoUsuarioResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoUsuarioResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoUsuarioResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TipoUsuarioRequest dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}