package com.fatec.horario.web.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.fatec.horario.domain.services.DisponibilidadeProfessorService;
import com.fatec.horario.dto.DisponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.horario.dto.DisponibilidadeProfessor.DisponibilidadeProfessorResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/disponibilidades-professores")
public class DisponibilidadeProfessorController {
    
    private final DisponibilidadeProfessorService service;

    public DisponibilidadeProfessorController(DisponibilidadeProfessorService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DisponibilidadeProfessorResponse criar(
            @RequestBody @Valid DisponibilidadeProfessorRequest request) {

        return service.criar(request);
    }

    @GetMapping
    public Page<DisponibilidadeProfessorResponse> listar(
            @RequestParam(required = false, name = "usuario") Long usuario,
            @RequestParam(required = false, name = "dia_semana") Long diaSemana,
            @RequestParam(required = false, name = "horario") Long horario,
            Pageable pageable) {

        return service.listar(usuario, diaSemana, horario, pageable);
    }

    @GetMapping("/{id}")
    public DisponibilidadeProfessorResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public DisponibilidadeProfessorResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DisponibilidadeProfessorRequest request) {

        return service.atualizar(id, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
