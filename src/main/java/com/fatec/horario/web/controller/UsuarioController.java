package com.fatec.horario.web.controller;

import com.fatec.horario.domain.entities.TipoUsuario;
import com.fatec.horario.domain.services.UsuarioService;
import com.fatec.horario.dto.usuario.UsuarioRequest;
import com.fatec.horario.dto.usuario.UsuarioResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping("/usuarios")
    public UsuarioResponse criar(@RequestBody @Valid UsuarioRequest dto) {
        TipoUsuario tipo = new TipoUsuario(dto.id_tipo_usuario(), null);
        return service.criar(dto, tipo);
    }

    @GetMapping("/usuarios")
    public Page<UsuarioResponse> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.listar(nome, email, status, PageRequest.of(page, size));
    }

    @GetMapping("/usuarios/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/usuarios/{id}")
    public UsuarioResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioRequest dto
    ) {
        TipoUsuario tipo = new TipoUsuario(dto.id_tipo_usuario(), null);
        return service.atualizar(id, dto, tipo);
    }

    @DeleteMapping("/usuarios/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}