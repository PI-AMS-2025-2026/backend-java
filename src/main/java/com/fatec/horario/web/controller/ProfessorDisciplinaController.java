package com.fatec.horario.web.controller;

import com.fatec.horario.domain.services.ProfessorDisciplinaService;
import com.fatec.horario.dto.ProfessorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.horario.dto.ProfessorDisciplina.ProfessorDisciplinaResponse;
import com.fatec.horario.infrastructure.mappers.ProfessorDisciplinaMapper;

import jakarta.validation.Valid;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professores-disciplinas")
public class ProfessorDisciplinaController {

    private final ProfessorDisciplinaService service;

    public ProfessorDisciplinaController(ProfessorDisciplinaService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfessorDisciplinaResponse criar(
            @RequestBody @Valid ProfessorDisciplinaRequest request) {

        return ProfessorDisciplinaMapper.toResponse(
                service.criar(request)
        );
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ProfessorDisciplinaResponse buscar(@PathVariable Long id) {
        return ProfessorDisciplinaMapper.toResponse(
                service.buscarPorId(id)
        );
    }

    // LIST + FILTROS + PAGINAÇÃO
    @GetMapping
    public Page<ProfessorDisciplinaResponse> listar(
            @RequestParam(required = false) Long usuario,
            @RequestParam(required = false) Long disciplina,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return service.listar(usuario, disciplina, pageable)
                .map(ProfessorDisciplinaMapper::toResponse);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ProfessorDisciplinaResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ProfessorDisciplinaRequest request) {

        return ProfessorDisciplinaMapper.toResponse(
                service.atualizar(id, request)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
