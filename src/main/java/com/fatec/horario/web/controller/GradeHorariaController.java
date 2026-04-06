package com.fatec.horario.web.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.fatec.horario.domain.services.GradeHorariaService;
import com.fatec.horario.dto.GradeHoraria.GradeHorariaRequest;
import com.fatec.horario.dto.GradeHoraria.GradeHorariaResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/grades-horarias")
public class GradeHorariaController {

    private final GradeHorariaService service;

    public GradeHorariaController(GradeHorariaService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public GradeHorariaResponse criar(
            @RequestBody @Valid GradeHorariaRequest request) {

        return service.criar(request);
    }

    @GetMapping
    public Page<GradeHorariaResponse> listar(
            @RequestParam(required = false) Long curso,
            @RequestParam(required = false, name = "periodo_letivo") Long periodoLetivo,
            @RequestParam(required = false) String status,
            Pageable pageable) {

        return service.listar(curso, periodoLetivo, status, pageable);
    }

    @GetMapping("/{id}")
    public GradeHorariaResponse buscarPorId(@PathVariable Long id) {

        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public GradeHorariaResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid GradeHorariaRequest request) {

        return service.atualizar(id, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}