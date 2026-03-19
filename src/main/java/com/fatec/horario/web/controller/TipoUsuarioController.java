package com.fatec.horario.web.controller;

import com.fatec.horario.domain.services.TipoUsuarioService;
import com.fatec.horario.dto.tipoUsuario.TipoUsuarioRequest;
import com.fatec.horario.dto.tipoUsuario.TipoUsuarioResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tipo-usuario")
@RequiredArgsConstructor
public class TipoUsuarioController {

    private final TipoUsuarioService service;

    @PostMapping
    public TipoUsuarioResponse criar(@RequestBody @Valid TipoUsuarioRequest dto) {
        return service.criar(dto);
    }

    @GetMapping
    public Page<TipoUsuarioResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.listar(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public TipoUsuarioResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public TipoUsuarioResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TipoUsuarioRequest dto
    ) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}