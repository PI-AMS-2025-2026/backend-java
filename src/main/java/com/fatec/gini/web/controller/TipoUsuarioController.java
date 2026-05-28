package com.fatec.gini.web.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.gini.domain.services.TipoUsuarioService;
import com.fatec.gini.dto.tipoUsuario.TipoUsuarioRequest;
import com.fatec.gini.dto.tipoUsuario.TipoUsuarioResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tipo-usuario")
@CrossOrigin
public class TipoUsuarioController {

    @Autowired
    private TipoUsuarioService service;

    @GetMapping
    public ResponseEntity<List<TipoUsuarioResponse>> listar(
            @RequestParam(required = false) String nome) {
        return ResponseEntity.ok(service.listar(nome));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoUsuarioResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoUsuarioResponse> criar(@RequestBody @Valid TipoUsuarioRequest dto) {

        TipoUsuarioResponse response = service.criar(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
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